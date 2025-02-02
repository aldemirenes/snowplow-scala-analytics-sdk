/*
 * Copyright (c) 2016-2019 Snowplow Analytics Ltd. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package com.snowplowanalytics.snowplow.analytics.scalasdk

// circe
import io.circe.syntax._
import io.circe.generic.semiauto._
import io.circe.{Encoder, Json, JsonObject, Decoder, DecodingFailure}
import io.circe.CursorOp.DownField

//cats
import cats.implicits._

// iglu
import com.snowplowanalytics.iglu.core.circe.CirceIgluCodecs._
import com.snowplowanalytics.iglu.core.{SchemaKey, SelfDescribingData}

object SnowplowEvent {

  /**
    * A JSON representation of an atomic event's unstruct_event field.
    *
    * @param data the unstruct event as self-describing JSON, or None if the field is missing
    */
  case class UnstructEvent(data: Option[SelfDescribingData[Json]]) extends AnyVal {
    def toShreddedJson: Option[(String, Json)] = {
      data.map {
        case SelfDescribingData(s, d) =>
          (transformSchema(Data.UnstructEvent, s.vendor, s.name, s.version.model), d)
      }
    }
  }

  /**
    * A JSON representation of an atomic event's contexts or derived_contexts fields.
    *
    * @param data the context as self-describing JSON, or None if the field is missing
    */
  case class Contexts(data: List[SelfDescribingData[Json]]) extends AnyVal {
    def toShreddedJson: Map[String, Json] = {
      data.groupBy(x => (x.schema.vendor, x.schema.name, x.schema.format, x.schema.version.model)).map {
        case ((vendor, name, _, model), d) =>
          (transformSchema(Data.Contexts(Data.CustomContexts), vendor, name, model), d.map {
            selfdesc => selfdesc.data
          }.asJson)
      }
    }
  }

  implicit final val contextsCirceEncoder: Encoder[Contexts] =
    Encoder.instance { contexts: Contexts =>
      if (contexts.data.isEmpty) JsonObject.empty.asJson
      else JsonObject(
        ("schema", Common.ContextsUri.toSchemaUri.asJson),
        ("data", contexts.data.asJson)
      ).asJson
    }

  implicit val contextsDecoder: Decoder[Contexts] = deriveDecoder[Contexts].recover {
    case DecodingFailure(_, List(DownField("data"), DownField(_))) => Contexts(List())
  }

  implicit final val unstructCirceEncoder: Encoder[UnstructEvent] =
    Encoder.instance { unstructEvent: UnstructEvent =>
      if (unstructEvent.data.isEmpty) Json.Null
      else JsonObject(
        ("schema", Common.UnstructEventUri.toSchemaUri.asJson),
        ("data", unstructEvent.data.asJson)
      ).asJson
    }

  implicit val unstructEventDecoder: Decoder[UnstructEvent] = deriveDecoder[UnstructEvent].recover {
    case DecodingFailure(_, List(DownField("data"), DownField(_))) => UnstructEvent(None)
  }

  /**
    * @param shredProperty Type of self-describing entity
    * @param vendor        Iglu schema vendor
    * @param name          Iglu schema name
    * @param model         Iglu schema model
    * @return the schema, transformed into an Elasticsearch-compatible column name
    */
  def transformSchema(shredProperty: Data.ShredProperty, vendor: String, name: String, model: Int): String = {
    // Convert dots & dashes in schema vendor to underscore
    val snakeCaseVendor = vendor.replaceAll("""[\.\-]""", "_").toLowerCase

    // Convert PascalCase in schema name to snake_case
    val snakeCaseName = name.replaceAll("""[\.\-]""", "_").replaceAll("([^A-Z_])([A-Z])", "$1_$2").toLowerCase

    s"${shredProperty.prefix}${snakeCaseVendor}_${snakeCaseName}_$model"
  }

  def transformSchema(shredProperty: Data.ShredProperty, schema: SchemaKey): String = transformSchema(shredProperty, schema.vendor, schema.name, schema.version.model)
}
