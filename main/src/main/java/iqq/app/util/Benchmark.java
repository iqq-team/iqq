 /*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 /**
 * Project  : WebQQCore
 * Package  : iqq.im.util
 * File     : Benchmark.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-4-7
 * License  : Apache License 2.0 
 */
package iqq.app.util;

 import org.slf4j.LoggerFactory;

 import java.util.Date;
 import java.util.Deque;
 import java.util.LinkedList;

 /**
 *
 * 一个工具类，可以用来跟踪程序效率问题
 * 目前暂时不支持多线程
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class Benchmark {
     private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Benchmark.class);
	private static Deque<BenchEntry> benchStack = new LinkedList<BenchEntry>();
	
	
	public static void start(String tag){
		BenchEntry entry = new BenchEntry();
		entry.tag = tag;
		entry.depth = benchStack.size();
		entry.start = new Date();
		benchStack.addFirst(entry);
	}
	
	public static void end(String tag){
		BenchEntry entry = benchStack.pollFirst();
		if(!entry.tag.equals(tag)){
			LOG.warn("Benchmark Not Match, tag spell mistake or forgot Benchmark.end(tag) invoke at somewhere ??");
			return;
		}
		
		entry.end = new Date();
		long used = entry.end.getTime() - entry.start.getTime();
		LOG.debug("Benchmark [" + entry.tag +" ] - Used: " + (used)  + " ms. ");
	}
	
	private static class BenchEntry{
		public long depth;
		public String tag;
		public Date start;
		public Date end;
	}
}
