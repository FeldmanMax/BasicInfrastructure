package services

import logger.ApplicationLogger

import sys.process.{ProcessLogger, _}

trait TCommander {
  def run[R](command: String, parser: String => Either[String, R]): Either[TerminalCommandResponse, R]
  def executeRun[R](command: String, parser: String => Either[String, R]): Either[String, R]
}

class TerminalCommander extends TCommander {
  def run[R](command: String, parser: String => Either[String, R]): Either[TerminalCommandResponse, R] = {
    ApplicationLogger.trace(s"run -> $command")
    try {
      val out = new StringBuffer()
      val err = new StringBuffer()

      command ! ProcessLogger((s) => out.append(s).append("\n"), (s) => err.append(s).append("\n"))
      prepareResponse(out.toString, err.toString, parser)
    }
    catch {
      case ex: Exception => ApplicationLogger.errorLeft(TerminalCommandResponse("", ex.getMessage))
    }
  }

  def executeRun[R](command: String, parser: String => Either[String, R]): Either[String, R] = {
    run[R](command, parser) match {
      case Left(left) => ApplicationLogger.errorLeft(left.toString)
      case Right(result) => Right(result)
    }
  }

  private def prepareResponse[R](out: String, err: String, parser: String => Either[String, R]): Either[TerminalCommandResponse, R] = {
    if(err.isEmpty)  parser(out) match {
      case Left(errorMessage) => ApplicationLogger.errorLeft(TerminalCommandResponse(out, errorMessage))
      case Right(response) => Right(response)
    }
    else                      ApplicationLogger.errorLeft(TerminalCommandResponse(out, err))
  }
}

case class TerminalCommandResponse(out: String, err: String) {
  override def toString: String = s"MY TERMINAL COMMAND RESPONSE \n Output: $out \nError: $err\n #### END ##### \n"
  def isSuccess: Boolean = err.isEmpty
}
