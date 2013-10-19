package org.hyperscala.examples.basic

import org.hyperscala.html._
import attributes.ButtonType
import org.hyperscala.web.Webpage
import org.hyperscala.realtime.Realtime
import org.hyperscala.examples.Example

/**
 * @author Matt Hicks <mhicks@outr.com>
 */
class RealtimeFormExample extends Example {
  Webpage().require(Realtime)
  Realtime.connectForm()
//  Realtime.connectPost()

  contents += new tag.Form(method = "get") {
    submitEvent.on {
      case evt => println("Form submitted with '%s'.".format(input.value()))
    }

    val input = new tag.Input(name = "field")
    contents += input
    contents += new tag.Button(buttonType = ButtonType.Submit, content = "Submit")
  }
}
