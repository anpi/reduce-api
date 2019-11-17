import com.twitter.finagle.Http
import com.twitter.finagle.service.ExpiringService
import com.twitter.util.Await
import io.finch._
import io.finch.circe._
import io.finch.syntax._
import io.circe.generic.auto._
import io.circe.parser.decode

import scala.io.Source

object Main extends App {
  val expirationService = new ExpirationService("6711")
  val recipeSearchService = new RecipeSearchService

  val pantry: Endpoint[List[Product]] = {
    get("products") { Ok(expirationService.products.results)}
  }

  val recipes: Endpoint[SearchRecipes] = {
    get("recipes" :: params[String]("i")) { (params: Seq[String]) =>
      Ok(recipeSearchService.query(List("6740")))
    }
  }

  Await.ready(Http.server.serve(":8081", (recipes :+: pantry).toService))
}