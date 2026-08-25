# 智能鱼缸控制系统

STM32F103C8T6 上的固件。板子自己采水温、pH、浊度、光照，结果打在 OLED 上。超了就叫，并控制加热、灯光、换水和喂食。ESP8266 用 MQTT 把数据送到云上。

仓库里没有姓名、学号，也没有云平台密钥。

## 能做什么

- DS18B20 测水温，太低就开加热棒
- ADC 读 pH、浊度、光照，超限蜂鸣器响
- 光线不够会开灯
- 到点或超限时继电器换水，步进电机喂食
- 三个键改阈值和喂食 / 换水间隔
- 连上阿里云后定时上报，APP 能手动喂一次
- 没有液位传感器，补水靠远程或按键

## 打开工程

Keil uVision 5 打开 `firmware/User/程序.uvprojx`，芯片选 `STM32F103C8`。编译结果在 `firmware/Output/`，这个目录不提交。

要连云的话，改 `firmware/Driver/esp8266.h` 里这些占位：

- `ProductKey`
- `DeviceName`
- `ClientId`
- `Password`（设备密钥）
- `mqttHostUrl`

填好的密钥别再提交回来。

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
| 灯光 | PA15 |
| 按键 | PB12–PB14 |

## 目录

```
firmware/
  User/       主循环、Keil 工程
  Driver/     传感器、电机、OLED、ESP8266
  System/     延时、串口
  CMSIS/      Cortex-M3 启动文件
  Libraries/  STM32 标准外设库
```

C、STM32F103、Keil MDK、ESP8266 MQTT、DS18B20、OLED、28BYJ-48
