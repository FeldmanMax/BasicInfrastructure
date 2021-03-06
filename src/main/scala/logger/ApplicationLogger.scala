package logger

import org.slf4j.event.Level
import org.slf4j.{Logger, LoggerFactory}

trait LoggerBase {
  def log[T <: AnyRef](level: Level, data: T)
  def log(level: Level, data: String)
}

class FileLogger extends LoggerBase {

  private lazy val typeToLogger: Map[String, Logger] = Map (
    "info" -> LoggerFactory.getLogger("info"),
    "warn" -> LoggerFactory.getLogger("warn"),
    "error" -> LoggerFactory.getLogger("error"),
    "trace" -> LoggerFactory.getLogger("trace"),
    "debug" -> LoggerFactory.getLogger("debug")
  )

  def log[T <: AnyRef](level: Level, data: T): Unit = logImpl(level, data.toString, typeToLogger(level.toString.toLowerCase))
  def log(level: Level, data: String): Unit = logImpl(level, data, typeToLogger(level.toString.toLowerCase))

  private def logImpl(level: Level, data: String, logger: Logger): Unit = {
    level match {
      case Level.DEBUG => logger.debug(data)
      case Level.ERROR => logger.error(data)
      case Level.INFO =>  logger.info(data)
      case Level.TRACE => logger.trace(data)
      case Level.WARN =>  logger.warn(data)
    }
  }
}

object FileLogger extends FileLogger

class MockedLogger extends LoggerBase {
  def log[T <: AnyRef](level: Level, data: T): Unit = {}
  def log(level: Level, data: String): Unit = {}
}

class ApplicationLogger(val loggers: List[LoggerBase]) extends LoggerBase {

  override def log[T <: AnyRef](level: Level, data: T): Unit = {
    log(level, data.toString)
  }

  override def log(level: Level, data: String): Unit = {
    loggers.foreach { logger => logger.log(level, data) }
  }
}

object ApplicationLoggerFactory {
  def get: LoggerBase = {
    val loggers: List[LoggerBase] = LoggerConfiguration.loggingDestinations.map {
      case FileDestination => new FileLogger()
      case DoNotLog =>        new MockedLogger()
      case _ =>               new MockedLogger()
    }
    new ApplicationLogger(loggers)
  }
}

object ApplicationLogger {
  private val logger: LoggerBase = ApplicationLoggerFactory.get

  def info[T <: AnyRef](data: T): Unit = logger.log(Level.INFO, data)
  def infoLeft[T <: AnyRef](data: T): Left[T, Nothing] = { info(data); Left(data) }
  def info(data: String): Unit = logger.log(Level.INFO, data)

  def debug[T <: AnyRef](data: T): Unit = logger.log(Level.DEBUG, data)
  def debugLeft[T <: AnyRef](data: T): Left[T, Nothing] = { debug(data); Left(data) }
  def debug(data: String): Unit = logger.log(Level.DEBUG, data)

  def error[T <: AnyRef](data: T): Unit = logger.log(Level.ERROR, data)
  def errorLeft[T <: AnyRef](data: T): Left[T, Nothing] = { error(data); Left(data) }
  def error(data: String): Unit = logger.log(Level.ERROR, data)

  def warn[T <: AnyRef](data: T): Unit = logger.log(Level.WARN, data)
  def warnLeft[T <: AnyRef](data: T): Left[T, Nothing] = { warn(data); Left(data) }
  def warn(data: String): Unit = logger.log(Level.WARN, data)

  def trace[T <: AnyRef](data: T): Unit = logger.log(Level.TRACE, data)
  def traceLeft[T <: AnyRef](data: T): Left[T, Nothing] = { trace(data); Left(data) }
  def trace(data: String): Unit = logger.log(Level.TRACE, data)
}