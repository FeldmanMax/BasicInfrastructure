package cache

import com.google.common.cache.{Cache, CacheBuilder}
import logger.ApplicationLogger

class TimedCache[Key<:Object, Value<:Object](val concurrencyLevel:  Int=6,
                                             val timeoutMinutes:    Int=10)
                                            (implicit ec: scala.concurrent.ExecutionContext) {
  import java.util.concurrent.{Callable, TimeUnit}

  import scala.concurrent.Future

  lazy val gCache: Cache[Key, Value] = CacheBuilder.newBuilder()
    .concurrencyLevel(concurrencyLevel)
    .softValues()
    .expireAfterWrite(timeoutMinutes, TimeUnit.MINUTES)
    .build[Key, Value]

  @inline def getWithDefault(key: Key, defaultValue: => Value): Value = gCache.get(key,
    new Callable[Value] {
      override def call: Value = defaultValue
    }
  )

  @inline def getWithError(key: Key): Either[String, Value] = {
    try{
      val result = gCache.getIfPresent(key)
      if(result == null)  ApplicationLogger.errorLeft(s"Key $key was not found")
      else                Right(result)
    }
    catch {
      case ex: Exception => ApplicationLogger.errorLeft(ex.getMessage)
    }
  }

  @inline def getOrAdd(key: Key, default: Value): Either[String, Value] = {
    if(gCache.getIfPresent(key) == null) {
      put(key, default)
    }
    getWithError(key)
  }

  @inline def getOrAdd(key: Key)(func: => Value): Either[String, Value] = {
    val result = gCache.getIfPresent(key)
    if(result == null)
      put(key, func)
    getWithError(key)
  }

  @inline def getAll(): Either[String, List[Value]] = {
    val list: List[Either[String, Value]] = getAllKeys().map(getWithError)
    if(list.exists(x=>x.isLeft))  ApplicationLogger.errorLeft(list.filter(x=>x.isLeft).map(x=>x.left.get) mkString "\\n")
    else                          Right(list.map(x=>x.right.get))
  }

  @inline def getAsyncWithDefault(key: Key, defaultValue: => Value): Future[Value] =
    Future { getWithDefault(key, defaultValue) }

  @inline def put(key: Key, value: Value): Unit = gCache.put(key, value)

  @inline def putAsync(key: Key, value: => Value): Future[Unit] = Future { gCache.put(key, value) }

  private def getAllKeys(): List[Key] = {
    import java.util
    def getAllKeyImpl(iterator: util.Iterator[Key]): List[Key] = {
      if(!iterator.hasNext) List.empty
      else                  iterator.next() :: getAllKeyImpl(iterator)
    }
    getAllKeyImpl(gCache.asMap().keySet().iterator())
  }
}

object TimedCache {
  @inline def apply[Key<:Object, Value<:Object](concurrencyLevel: Int=4, timeoutMinutes: Int=5)
                                               (implicit ec: scala.concurrent.ExecutionContext) =
    new TimedCache[Key, Value](concurrencyLevel, timeoutMinutes){}
}