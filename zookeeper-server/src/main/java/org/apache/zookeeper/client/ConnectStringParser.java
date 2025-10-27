/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.zookeeper.client;

import java.net.InetSocketAddress;
import java.util.ArrayList;

import org.apache.zookeeper.common.PathUtils;

/**
 * A parser for ZooKeeper Client connect strings.
 * 
 * This class is not meant to be seen or used outside of ZooKeeper itself.
 * 
 * The chrootPath member should be replaced by a Path object in issue
 * ZOOKEEPER-849.
 * 
 * @see org.apache.zookeeper.ZooKeeper
 */
public final class ConnectStringParser {
    private static final int DEFAULT_PORT = 2181;

    private final String chrootPath;

    private final ArrayList<InetSocketAddress> serverAddresses = new ArrayList<InetSocketAddress>();

    /**
     * 
     * @throws IllegalArgumentException
     *             for an invalid chroot path.
     */
    public ConnectStringParser(String connectString) {
        // parse out chroot, if any

        // 这个连接字符串，如果你要加 chroot，就要加到 /
        int off = connectString.indexOf('/');
        if (off >= 0) {
            // 截取第一个 / 往后的 path，这个就是 chrootPath
            String chrootPath = connectString.substring(off);
            // ignore "/" chroot spec, same as null
            // 如果长度是 1，说明末尾只有一个 /。例如 127.0.0.1:2181/，就跟没配一样
            if (chrootPath.length() == 1) {
                this.chrootPath = null;
            }
            // 验证 chrootPath，不能以 / 结尾
            else {
                PathUtils.validatePath(chrootPath);
                this.chrootPath = chrootPath;
            }

            // 分离 connectString，这个才是真正的连接字符串
            connectString = connectString.substring(0, off);
        } else {
            this.chrootPath = null;
        }

        // 使用 , 分开
        String hostsList[] = connectString.split(",");
        for (String host : hostsList) {
            int port = DEFAULT_PORT;
            int pidx = host.lastIndexOf(':');
            if (pidx >= 0) {
                // otherwise : is at the end of the string, ignore
                // 你的 ':' 总不能是最后一个字符吧！
                // 解析 port
                if (pidx < host.length() - 1) {
                    port = Integer.parseInt(host.substring(pidx + 1));
                }
                // 分离出 host
                host = host.substring(0, pidx);
            }
            serverAddresses.add(InetSocketAddress.createUnresolved(host, port));
        }
    }

    public String getChrootPath() {
        return chrootPath;
    }

    public ArrayList<InetSocketAddress> getServerAddresses() {
        return serverAddresses;
    }
}