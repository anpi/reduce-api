import io.circe
import io.circe.Decoder
import io.circe.parser.decode
import io.circe.generic.auto._
import sttp.client._
import io.circe.syntax._

import scala.io.Source



class ExpirationService(customer: String) {

  val apiKey = "f9472b9a51bb44dfbf647aab5ba2771e"
  implicit val backend = HttpURLConnectionBackend()
  val receiptDataResource = Source.fromResource("Junction_data_sample.csv")
  val customerReceipts = receiptDataResource.getLines().filter((line) => {
    val fields = line.split(";")
    fields(6) == "\"25-34\"" && fields(7) == "6711" && fields(8) == "\"Q_1-3\"" && fields(9) == "\"E_4-7\""
  })
  val eans = customerReceipts.map((r) => r.split(";")(4).replaceAll("\"", ""))

  val eanList = eans.toList
  val payload = ProductSearchRequest(Filters(eanList), View(0, 1000)).asJson

  val request = basicRequest
    .header("Ocp-Apim-Subscription-Key", apiKey)
    .header("Content-Type", "application/json")
    .body(payload.toString)
    .post(uri"https://kesko.azure-api.net/v1/search/products")
  val response = request.send()
  val respString = response.body.toOption.get.mkString
  val searchResults = decode[ProductSearchResults](respString)
  val products = searchResults.getOrElse(null)
}

// Request
case class ProductSearchRequest(filters: Filters, view: View)
case class Filters(ean: List[String])
case class View(offset: Int, limit: Int)

// Response
case class ProductSearchResults(results: List[Product])
case class Product(marketingName: MarketingName, segment: Segment, pictureUrls: List[ProductPictureUrl])
case class MarketingName(finnish: String)
case class ProductPictureUrl(original: String)
case class Segment(id: String)
