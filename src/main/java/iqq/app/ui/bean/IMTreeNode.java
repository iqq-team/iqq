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
 * Project  : IQQ_V2.1
 * Package  : iqq.app.ui.bean
 * File     : IMTreeNode.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-4-7
 * License  : Apache License 2.0 
 */
package iqq.app.ui.bean;

import iqq.app.util.Benchmark;

import java.util.Collections;
import java.util.Comparator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 *
 * 自定义的树节点，主要提供了排序功能
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class IMTreeNode extends DefaultMutableTreeNode{
	private static final long serialVersionUID = 1L;
	private Comparator<TreeNode> comparator;
	
	public IMTreeNode() {
		super();
	}
	public IMTreeNode(Object userObject) {
		super(userObject);
	}
	
	public IMTreeNode(Object userObject, Comparator<TreeNode> comparator) {
		super(userObject);
		this.comparator = comparator;
	}
	
	public void sortChildren(){
		if(comparator != null && children != null){
			Benchmark.start("sortChildren");
			Collections.sort(children, comparator);
			Benchmark.end("sortChildren");
		}
		
	}
	
	public Comparator<TreeNode> getComparator() {
		return comparator;
	}
	
	public void setComparator(Comparator<TreeNode> comparator) {
		this.comparator = comparator;
	}
	

}
