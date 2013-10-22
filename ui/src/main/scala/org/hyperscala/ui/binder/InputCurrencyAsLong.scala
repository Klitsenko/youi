package org.hyperscala.ui.binder

import language.reflectiveCalls
import org.hyperscala.html._
import java.text.NumberFormat
import org.hyperscala.ui.dynamic.Binder
import org.hyperscala.realtime.RealtimeEvent

/**
 * @author Matt Hicks <mhicks@outr.com>
 */
class InputCurrencyAsLong extends Binder[tag.Input, Long] {
  val formatter = NumberFormat.getCurrencyInstance

  def bind(input: tag.Input) = {
    input.value.change.on {
      case evt => {
        val s = input.value().collect {
          case c if (c.isDigit || c == '.') => c
        }
        val split = s.split('.')
        val l = if (split.length == 1) {
          split(0) + "00"
        } else if (split.length == 2) {
          split(1) match {
            case v if (v.length == 1) => split(0) + v + "0"
            case v if (v.length == 2) => split(0) + v
          }
        } else {
          "0"
        }
        valueProperty := l.toLong
      }
    }
    valueProperty.change.on {
      case evt => {
        val s = valueProperty() match {
          case 0L => formatter.format(0.0)
          case v => formatter.format(v / 100.0)
        }
        input.value := s
      }
    }

    input.changeEvent := RealtimeEvent()
  }
}
