package com.lpc.controllers

import com.lpc.services.auth.DefaultEnv
import com.mohiva.play.silhouette.api.Silhouette
import javax.inject.Inject
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class LpcController @Inject()(cc: ControllerComponents,
                              silhouette: Silhouette[DefaultEnv])
  extends AbstractController(cc) {

  def jsonOk() = jsonOkWithStatus(Ok)
  def jsonOk[R: Writes](r: R) = withCustomHeader(Ok(Json.toJson(r)))
  def jsonOkWithStatus(status: Status) = withCustomHeader(status(Json.obj("status" -> "SUCCESS")))
  def jsonOkWithNonconformingResponse[R: Writes](key: String, r: R) =
    withCustomHeader(Ok(Json.obj(key -> Json.toJson(r))))

  def jsonFail(status: Status) = withCustomHeader(status(Json.obj("status" -> "FAILURE")))
  def jsonFail(status: Status, message: String) = withCustomHeader(status(Json.obj("message" -> message)))
  def jsonFail[R: Writes](status: Status, r: R) = withCustomHeader(status(Json.obj("message" -> Json.toJson(r))))

  def jsonWithAuth[BODY] = new {
    def apply(body: (Request[_], BODY) => Future[Result])
             (implicit reads: Reads[BODY]): Action[JsValue] =
      silhouette.SecuredAction.async(controllerComponents.parsers.tolerantJson) { implicit request =>
        request.body.validate[BODY].fold(
          handleErrors(request, _),
          handleParseResults(body, request)
        )
      }
  }

  def jsonWithoutAuth[BODY] = new {
    def apply(body: (Request[_], BODY) => Future[Result])
             (implicit reads: Reads[BODY]): Action[JsValue] =
      trackMetrics.async(controllerComponents.parsers.tolerantJson) { implicit request =>
        request.body.validate[BODY].fold(
          handleErrors(request, _),
          handleParseResults(body, request)
        )
      }
  }


  private val trackMetrics = new TrackMetrics()

  private[this] def handleErrors(request: Request[JsValue], errors: collection.Seq[(JsPath, collection.Seq[JsonValidationError])]) = {
    Future.successful(jsonFail(BadRequest, JsError.toJson(errors)))
  }

  private def handleParseResults[BODY](bodyParser: (Request[_], BODY) => Future[Result], request: Request[JsValue]) =
    bodyParser(request, _: BODY)

  private def withCustomHeader(r: Result) = r.withHeaders(("Access-Control-Allow-Origin", "*"),
    ("Access-Control-Allow-Methods", "OPTIONS, GET, POST, PUT, DELETE, HEAD"),
    ("Access-Control-Allow-Headers", "Accept, Content-Type, Origin, X-Json, X-Prototype-Version, X-Requested-With"),
    ("Access-Control-Allow-Credentials", "true"))

  private class TrackMetrics extends ActionBuilder[Request, AnyContent] {
    override def parser: BodyParser[AnyContent] = controllerComponents.parsers.defaultBodyParser

    override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = block(request)

    override protected def executionContext: ExecutionContext = ExecutionContext.global
  }

}