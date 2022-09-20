package me.aylias.plugins.dotwav.mm;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class ActionbarFilter implements Filter {

  @Override
  public boolean isLoggable(LogRecord record) {
    boolean block = record.getMessage()
                          .contains("Showing new actionbar title for");

    return !block;
  }


//  public Result getDecision(LogEvent event) {
//    boolean block = event.getMessage()
//                         .getFormattedMessage()
//                         .contains("Showing new actionbar title for");
//
//    return block ? Result.DENY : Result.NEUTRAL;
//  }
}
