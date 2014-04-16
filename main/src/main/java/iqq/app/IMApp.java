package iqq.app;
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

import com.alee.laf.WebLookAndFeel;
import iqq.app.core.context.IMContext;
import iqq.app.ui.frame.LoginFrame;

/**
 * IQQ客户主类
 * Project  : iqq
 * Author   : solosky < solosky772@qq.com >
 * Created  : 4/13/14
 * License  : Apache License 2.0
 */
public class IMApp {
    public static void main(String[] args){
        IMContext.getIoc();

        launch();
    }

    private static void launch() {
        WebLookAndFeel.install();
        new LoginFrame().setVisible(true);
    }
}
