package core

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec}
import org.scalatest.Matchers

class ReferenceSpec extends FunSpec with Matchers {

  it("looks up type when a model") {
    val json = """
    {
      "base_url": "http://localhost:9000",
      "name": "Api Doc",
      "models": {
        "user": {
          "fields": [
            { "name": "guid", "type": "uuid" }
          ]
        },
        "account": {
          "fields": [
            { "name": "user", "type": "user" }
          ]
        }
      }
    }
    """
    val validator = ServiceDescriptionValidator(json)
    validator.errors.mkString("") should be("")
    val account = validator.serviceDescription.get.models.find { _.name == "account" }.get
    account.fields.head.name should be("user")
    account.fields.head.fieldtype match {
      case ModelFieldType(model: Model) => {
        model.name should be("user")
      }
      case f: Any => {
        fail("expected uuid: " + f)
      }
    }
  }


  it("looks up type when referencing an existing field") {
    val json = """
    {
      "base_url": "http://localhost:9000",
      "name": "Api Doc",
      "models": {
        "user": {
          "fields": [
            { "name": "guid", "type": "uuid" }
          ]
        },
        "account": {
          "fields": [
            { "name": "user", "type": "reference[user]" }
          ]
        }
      }
    }
    """
    val validator = ServiceDescriptionValidator(json)
    validator.errors.mkString("") should be("")
    val account = validator.serviceDescription.get.models.find { _.name == "account" }.get
    account.fields.head.name should be("user")
    account.fields.head.fieldtype match {
      case ReferenceFieldType(referencedModelName: String) => {
        referencedModelName should be("user")
      }
      case f: Any => {
        fail("expected uuid: " + f)
      }
    }
  }

}
