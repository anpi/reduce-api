import io.circe.Decoder
import io.circe.parser.decode
import io.circe.generic.extras._
import io.circe.generic.auto._
import io.circe.syntax._
import sttp.client._

case class SearchRecipes(results: List[KeskoRecipe])

case class KeskoRecipe(name: String, ingredients: List[IngredientGroup], pictureUrls: List[PictureUrl])
case class IngredientGroup(subSectionIngredients: List[List[SubSectionIngredient]])
case class SubSectionIngredient(name: String, ingredientType: Option[String], ingredientTypeName: Option[String])
case class PictureUrl(url: String)

object KeskoRecipe {
  implicit val decodeRecipe: Decoder[KeskoRecipe] =
    Decoder.forProduct3("Name", "Ingredients", "PictureUrls")(KeskoRecipe.apply)
}

object IngredientGroup {
  implicit val decodeIngredient: Decoder[IngredientGroup] =
    Decoder.forProduct1("SubSectionIngredients")(IngredientGroup.apply)
}

object SubSectionIngredient {
  implicit val decodeSubsectionIngredient: Decoder[SubSectionIngredient] =
    Decoder.forProduct3("Name", "IngredientType", "IngredientTypeName")(SubSectionIngredient.apply)
}

object PictureUrl {
  implicit val decodePictureUrl: Decoder[PictureUrl] =
    Decoder.forProduct1("Original")(PictureUrl.apply)
}



class RecipeSearchService {
  implicit val backend = HttpURLConnectionBackend()
  val apiKey = "f9472b9a51bb44dfbf647aab5ba2771e"

  def query(ingredientTypes: List[String]) = {
    val payload = RecipeSearchRequest(RecipeFilters(ingredientTypes), View(0, 1000)).asJson

    val request = basicRequest
      .header("Ocp-Apim-Subscription-Key", apiKey)
      .header("Content-Type", "application/json")
      .body(payload.toString)
      .post(uri"https://kesko.azure-api.net/v1/search/recipes")
    val response = request.send()
    response.body.map(decode[SearchRecipes](_)).getOrElse(null).getOrElse(null)
  }
}

case class RecipeSearchRequest(filters: RecipeFilters, view: View)
case class RecipeFilters(ingredientType: List[String])