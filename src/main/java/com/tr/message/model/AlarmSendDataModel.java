package com.tr.message.model;

import lombok.Data;

import java.io.Serializable;

/***
 * WebSocket 推送数据 Model
 **/
@Data
public class AlarmSendDataModel implements Serializable {

    /***
     * id
     **/
    private String id;

    /***
     * 公司Id
     **/
    private String companyId;

    /***
     * 公司名称
     **/
    private String companyName;

    /***
     * 设备编号
     **/
    private String deviceCode;

    /***
     * 设备编号
     **/
    private String deviceId;

    /***
     * 设备名称
     **/
    private String deviceName;

    /***
     * 设备型号
     **/
    private String deviceModel;

    /***
     * 设备类型
     **/
    private String deviceType;

    /***
     * 联系电话
     **/
    private String phone;

    /***
     * 主机回路号
     **/
    private String hostLoopNum;

    /*** AlarmTypeEnum
     * 报警类型
     **/
    private String alarmType;

    /*** AlarmTypeEnum
     * 报警类型
     **/
    private String alarmTypeStr;

    /***  AlarmSubTypeEnum
     * 报警子类型【水流、水压、电压、温度 等】
     **/
    private String alarmSubType;

    /***  AlarmSubTypeEnum
     * 报警子类型【水流、水压、电压、温度 等】
     **/
    private String alarmSubTypeStr;

    /***
     * 报警详情
     **/
    private String alarmInfo;

    /***
     * 楼座
     **/
    private String installBuild;

    /***
     * 安装位置
     */
    private String installLocal;

    /***
     * 建筑Id
     **/
    private String installBuildId;

    /***
     * 楼层Id
     **/
    private String installFloorId;

    /*** MarkerTagTypeEnum
     * 标记类型【1:监控 2:测试 3:维保 4:误报 5:屏蔽】
     **/
    private String state;

    /*** MarkerTagTypeEnum
     * 设备状态 Str【1:监控 2:测试 3:维保 4:误报 5:屏蔽】
     **/
    private String stateStr;

    /*
     * 忽略类型【1:报警声 2:点位闪动 3:系统弹窗 4:视频弹窗 5:火警处置流程】 拼接字符串
     */
    private String ignoreType;

    /*** AlarmHandleFlagEnum
     * 处理标志【0：未处理 1：处理】
     **/
    private String handleFlag;

    /***
     * 处理人userId
     **/
    private String handleUserId;

    /***
     * 处理结果
     **/
    private String handleResult;

    /***
     * 创建时间
     **/
    private String createTime;

    /***
     * 更新时间
     **/
    private String updateTime;

    /***
     * 管理人
     **/
    private String manage;

    /***
     * 管理人联系方式
     **/
    private String manageTelephone;

    /***
     * 责任人
     **/
    private String charge;

    /***
     * 责任人联系方式
     **/
    private String chargeTelephone;

    /***
     * 中层消防主管
     **/
    private String fireChief;

    /***
     * 中层消防主管联系方式
     **/
    private String fireChiefTelephone;

    /***
     * 经度
     **/
    private String lon;

    /***
     * 纬度
     **/
    private String lat;

    /***
     * 摄像头Id
     **/
    private String cameraId;

    /***
     * 备注
     **/
    private String remark;

    /***
     * 误报次数
     **/
    private String falseAlarmCount;

    /***
     * 上次误报处理原因
     **/
    private String falseAlarmHandleReasonLast;
}
