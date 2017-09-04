package org.dbpedia.quad.processing

import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue, ThreadPoolExecutor, TimeUnit}


/**
  * Created by chile on 12.06.17.
  */
class WorkerExecutor(corePoolSize: Int,
                     maximumPoolSize: Int,
                     keepAliveTime: Long,
                     unit: TimeUnit,
                     queueCapacity: Int)
  extends ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, new ArrayBlockingQueue[Runnable](queueCapacity) {
    override def offer(e: Runnable): Boolean = {
      if(this.size() == queueCapacity)
        false
      else {
        put(e)
        true
      }
    }
  }) {
}
