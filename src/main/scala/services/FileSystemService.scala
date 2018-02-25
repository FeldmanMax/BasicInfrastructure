package services

import java.io.{File, PrintWriter}

import logger.ApplicationLogger

import scala.io.{BufferedSource, Source}
import scala.util.matching.Regex

class FileSystemService {
  def loadFile(path: String): Either[String, String] = {
    ApplicationLogger.trace(s"${this.getClass} -> load file -> $path")
    try {
      val buffer: BufferedSource = Source.fromFile(path)
      try {
        val retValue: String = (buffer.getLines() mkString " ").toString
        ApplicationLogger.trace(s"${this.getClass} -> load file -> $path -> FINISH OK!")
        Right(retValue)
      }
      finally buffer.close()
    }
    catch {
      case ex: Exception => ApplicationLogger.errorLeft(ex.getMessage)
    }
  }

  def write(path: String, data: String): Either[String, Boolean] = {
    val file: File = new File(path)
    if(file.canWrite) {
      try{
        val printer_writer = new PrintWriter(new File(path))
        printer_writer.write(data)
        printer_writer.close()
        Right(true)
      }
      catch {
        case ex: Exception => ApplicationLogger.errorLeft(ex.getMessage)
      }
    }
    else  ApplicationLogger.errorLeft("File is readonly")
  }

  def filesByExtension(path: String, extension: String): Either[String, List[String]] = {
    try {
      val files: List[File] = new File(path).listFiles().filter(_.isFile).toList.filter(file => file.getName.contains(extension))
      val names: List[String] = files.map(x=>x.getName)
      Right(names)
    }
    catch {
      case ex: Exception => ApplicationLogger.errorLeft(ex.getMessage)
    }
  }

  def deleteFile(path: String): Either[String, Boolean] = {
    try{
      Right(new File(path).delete())
    }
    catch {
      case ex: SecurityException => ApplicationLogger.errorLeft(ex.getMessage)
    }
  }

  def deleteFiles(base_location: String, nameRegex: Regex): Either[String, Boolean] = {
    val filesToDelete: List[File] = new File(base_location).listFiles().toList.filter(file => nameRegex.findFirstIn(file.getName).nonEmpty)
    if(filesToDelete.map { file => file.delete() }.distinct.contains(false))  ApplicationLogger.errorLeft("Failed to delete some files")
    else                                                                      Right(true)
  }

  def createFile(name: String, is_readable: Boolean = true, is_writeable: Boolean = true): Either[String, File] = {
    val file: File = new File(name)
    file.setReadable(is_readable)
    file.setWritable(is_writeable)
    try{
      file.createNewFile()
      Right(file)
    }
    catch {
      case ex: Exception => ApplicationLogger.errorLeft(ex.getMessage)
    }
  }
}
