package wikigraph

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import Articles.ArticleId


/**
  * Analyze the graph of Wikipedia Articles
  *
  * @param client the wikipedia client providing access to the data.
  */
final class Wikigraph(client: Wikipedia):
  import wikigraph.WikiResult.*

  /**
    * Retrieves the names of the articles linked in a page.
    * 
    * @param of the id of the page from which the links are retrieved
    * 
    * Hint: Use the methods that you implemented in WikiResult.
    */
  def namedLinks(of: ArticleId): WikiResult[Set[String]] =
    client.linksFrom(of).flatMap { articles =>
      traverse(articles.toSeq)(client.nameOfArticle(_)).map(_.toSet)
    }


  /**
    * Computes the distance between two articles using breadth first search.
    * 
    * @param start compute the distance from this node to `target`
    * @param target compute the distance from `start` to this node
    * @param maxDepth stop if the depth exceeds this value
    * 
    * @return an asynchronous computation that might fail. If the maximal distance
    *         is exceeded during the search, the result is None
    * 
    * Note: if a domain error occurs when jumping from node to node,
    *       fallback by ignoring the problematic node. On the other hand,
    *       any system failure just ends the algorithm by returning that
    *       system failure.
    * 
    * Hint: More information is provided in the description of the assignment
    *       Use the `enqueue` and `dequeue` methods of `Queue`.
    */
  def breadthFirstSearch(start: ArticleId, target: ArticleId, maxDepth: Int): WikiResult[Option[Int]] =
    import scala.collection.immutable.Queue
    /**
      * This recursive method iterates on the graph.
      * 
      * The algorithm is detailed in the assignment description.
      * - When the queue is empty or the maxDepth is exceeded (in the next element of the queue),
      *   the search fails with None
      * - Otherwise a node is retrieved from the queue and its neighbors fetched from the dataset.
      *   The search succeeds if `target` is in this set of neighbors.
      *   Otherwise we recursively search after modifying `unknowns` and adding the unknown
      *   neighbors to the queue with the correct distance.
      * 
      * @param visited keep the nodes the are already visited, used no to iterate infinitely on
      *        graph cycles
      * @param q the next nodes to visit and their distance from `start`
      */
    def iter(visited: Set[ArticleId], q: Queue[(Int, ArticleId)]): WikiResult[Option[Int]] =
      q.dequeueOption match {
        case None                                               => successful(None)
        case Some(((n, _), _))         if n > maxDepth          => successful(None)
        case Some(((n, art), _))       if art == target         => successful(Some(n))
        case Some(((n, art), restOfq))                          =>
          client.linksFrom(art).fallbackTo(successful(Set.empty)).flatMap { articles =>
            traverse(articles.toSeq) { article =>
              if visited contains article then successful(None)
              else iter(visited + art, restOfq enqueue (n + 1, article))
            }.map(_.foldLeft(None: Option[Int]){ (acc, opt) => (acc, opt) match {
                                                  case (None, Some(i)) => Some(i)
                                                  case (Some(i), _)    => Some(i)
                                                  case _               => None
                                                }
                  })
          }
      }
    if start == target then WikiResult.successful(Some(0))
    else iter(Set(start), Queue(0->start))

  /**
    * Computes the distances between some pages provided the list of their titles.
    * Do not compute the distance from page and itself.
    * 
    * @param titles names of the articles
    * @param maxDepth stop the search this value of distance is exceeded
    * 
    * @return An asynchronous computation of the following form:
    *         Seq((distanceFromTitle, distanceToTitle, distance), ...)
    * 
    * Hint: You should use the methods that you implemented on WikiResult as well as
    *       breadthFirstSearch
    */
  def distanceMatrix( titles: List[String]
                    , maxDepth: Int = 50
                    ): WikiResult[Seq[(String, String, Option[Int])]] =

    def distanceRow( start: String
                   , titles: List[String]
                   , maxDepth: Int = 50
                   ): WikiResult[Seq[(String, String, Option[Int])]] =
      traverse(titles) { target =>
        (client.searchId(start) zip client.searchId(target))
          .flatMap((a, b) => breadthFirstSearch(a, b, maxDepth).map(res =>
                     (start, target, res)
                   ))
      }
    traverse(titles)(distanceRow(_, titles, maxDepth))
      .map(_.flatten)
      .map(_.filter((a, b, _) => a != b))

end Wikigraph
