# 智能鱼缸控制系统

基于 STM32F103C8T6 的嵌入式固件。系统采集水温、pH、浊度与光照，在 OLED 上显示，并按阈值控制加热、照明、换水与自动投喂。ESP8266 通过 MQTT 将数据上报至云平台。

本仓库不包含真实姓名、学号及云平台密钥。

## 功能

- DS18B20 测量水温，低于下限时启动加热棒
- ADC 采集 pH、浊度与光照，超限时蜂鸣器报警
- 光照不足时开启照明
- 定时或超限时由继电器执行换水，步进电机执行投喂
- 三键本地设置阈值及投喂 / 换水周期
- 接入阿里云物联网平台后定时上报，并可接收远程投喂指令
- 不含液位传感器，补水由远程或按键控制

## 编译

使用 Keil uVision 5 打开 `firmware/User/程序.uvprojx`，目标器件为 `STM32F103C8`。编译输出位于 `firmware/Output/`，该目录不纳入版本库。

连接云平台前，请将 `firmware/Driver/esp8266.h` 中的占位符替换为控制台中的实际参数：

- `ProductKey`
- `DeviceName`
- `ClientId`
- `Password`（设备密钥）
- `mqttHostUrl`

请勿将填入真实密钥后的文件提交回仓库。

## 引脚

| 功能 | 引脚 |
| --- | --- |
| DS18B20 | PB0 |
| pH | PA0（ADC_Channel_0） |
| 光照 | PA1（ADC_Channel_1） |
| 浊度 | PB1（ADC_Channel_9） |
| OLED I2C | PC13 SDA、PC14 SCL |
| ESP8266 UART1 | PA9 TX、PA10 RX |
| 步进电机 | PB6–PB9 |
| 出水 / 进水继电器 | PB3 / PB4 |
| 蜂鸣器 | PB5 |
| 加热 | PB10 |
| 照明 | PA15 |
| 按键 | PB12–PB14 |

## 目录结构

```
firmware/
  User/       主程序与 Keil 工程
  Driver/     传感器、电机、OLED、ESP8266
  System/     延时与串口
  CMSIS/      Cortex-M3 启动文件
  Libraries/  STM32 标准外设库
```

**技术栈** C、STM32F103、Keil MDK、ESP8266 MQTT、DS18B20、OLED、28BYJ-48
