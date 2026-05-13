package com.tr.message.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class WebSocketEntity {

    /***
     * websocket数据
     */
    private String message;

    /***
     * 数据类型 1:心跳  2:WS  3：历史WS
     */
    private Integer type;

}
