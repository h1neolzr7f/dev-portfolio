#ifndef _ESP8266_H_
#define _ESP8266_H_

#include "sys.h"

#define REV_OK		0	//接收完成标志
#define REV_WAIT	1	//接收未完成标志


void ESP8266_Init(void);
void ESP8266_Clear(void);
_Bool ESP8266_SendCmd(char *cmd, char *res, u16 time);
void ESP8266_SendData(unsigned char *data, unsigned short len);
unsigned char *ESP8266_GetIPD(unsigned short timeOut);


//************************云代码*********************//

#define ProductKey    "YOUR_PRODUCT_KEY"
#define DeviceName    "YOUR_DEVICE_NAME"
#define ClientId      "YOUR_CLIENT_ID"
#define Password      "YOUR_DEVICE_SECRET"
#define mqttHostUrl   "YOUR_PRODUCT_KEY.iot-as-mqtt.cn-shanghai.aliyuncs.com"
#define port          "1883" 


extern uint8_t SmartConfig;
extern uint8_t wifi_status;
extern uint16_t time_flag_1ms;

typedef struct{			//时间结构体
	uint16_t year;
	uint8_t month;
	uint8_t day;
	uint8_t week;
	uint8_t hour;
	uint8_t minute;
	uint8_t second;
}Time_Get;
char *ESP8266_GetRece(void);
void ESP8266LinkloT(void);		//连接阿里云，并订阅主题

_Bool ESP8266_Status(void);

#endif
