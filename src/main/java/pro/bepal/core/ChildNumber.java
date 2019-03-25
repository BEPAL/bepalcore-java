/*
 * Copyright (c) 2018-2019, BEPAL
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of California, Berkeley nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package pro.bepal.core;

import pro.bepal.util.ByteUtil;

import java.text.MessageFormat;
import java.util.List;

/**
 * 推导公钥时路径的编号
 */
public class ChildNumber {
    private int intPath;
    public boolean hardened;

    public ChildNumber(int path) {
        this(path, false);
    }

    public ChildNumber(int path, boolean hardened) {
        intPath = path;
        this.hardened = hardened;
    }

    /**
     * 获取标准公钥使用
     */
    public long getKeyPath() {
        long temp = intPath;
        if (hardened) {
            temp += 0x80000000;
        }
        return temp;
    }

    /**
     * SECP256k1家族的路径
     */
    public byte[] getPath() {
        long temp = intPath;
        if (hardened) {
            temp += 0x80000000;
        }
        return ByteUtil.intToBytesLE(temp);
    }

    /**
     * Nem的路径
     */
    public byte[] getPathNem() {
        long temp = intPath;
        return ByteUtil.intToBytes(temp);
    }

    /**
     * BTM的路径
     */
    public byte[] getPathBtm() {
        return ByteUtil.longToBytes(intPath);
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0} {1}", intPath, hardened ? "H" : "");
    }
}
