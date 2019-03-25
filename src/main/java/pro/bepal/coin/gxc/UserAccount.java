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
package pro.bepal.coin.gxc;

import pro.bepal.util.ByteUtil;

public class UserAccount {
    public static final String PROXY_TO_SELF = "1.2.5";

    protected String id;

    protected int space;
    protected int type;
    protected long instance;

    /**
     * Constructor that expects a user account in the string representation.
     * That is in the 1.2.x format.
     * @param id: The string representing the user account.
     */
    public UserAccount(String id) {
        this.id = id;
        String[] parts = id.split("\\.");
        if(parts.length == 3){
            this.space = Integer.parseInt(parts[0]);
            this.type = Integer.parseInt(parts[1]);
            this.instance = Long.parseLong(parts[2]);
        }
    }

    public String getId() {
        return id;
    }

    public byte[] toByte() {
        return ByteUtil.uvarToBytes(this.instance);
    }

    /**
     *
     * @return: A String containing the full object apiId in the form {space}.{type}.{instance}
     */
    public String getObjectId(){
        return String.format("%d.%d.%d", space, type, instance);
    }

    @Override
    public String toString() {
        return id;
    }
}
