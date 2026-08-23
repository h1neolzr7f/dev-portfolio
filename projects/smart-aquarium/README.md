# 智能鱼缸控制系统

STM32F103C8T6 固件：本地采集水温、pH、浊度、光照，OLED 显示，按阈值控制加热、灯光、换水和自动投喂，再通过 ESP8266 用 MQTT 上报到云平台。公开仓库不放真实姓名、学号和云平台密钥。

## 功能

- DS18B20 测水温，超下限开加热棒
- ADC 采集 pH、浊度、光照；超限蜂鸣器报警
- 光照不足时开灯
- 定时或超限时用继电器做换水；步进电机定时投喂
- 三个按键在本地改阈值和投喂 / 换水周期
- ESP8266 连阿里云物联网平台：定时上报，APP 可下发手动投喂等指令
- 不含液位传感器，补水由远端或按键侧控制

## 打开工程

用 Keil uVision 5 打开：

`firmware/User/程序.uvprojx`

目标芯片 `STM32F103C8`。编译产物写到 `firmware/Output/`，这个目录不会提交。

连云之前，把 `firmware/Driver/esp8266.h` 里的占位改成你自己控制台里的：

- `ProductKey`
- `DeviceName`
- `ClientId`
- `Password`（设备密钥）
- `mqttHostUrl`

不要把填好的密钥再提交回来。

## 主要引脚

| 功能 | 引脚 |
| --- | --- |
| DS18B20 | PB0 |
| pH ADC | PA0（ADC_Channel_0） |
| 光照 ADC | PA1（ADC_Channel_1） |
| 浊度 ADC | PB1（ADC_Channel_9） |
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

## 技术栈

C、STM32F103、Keil MDK、ESP8266 MQTT、DS18B20、OLED、28BYJ-48
