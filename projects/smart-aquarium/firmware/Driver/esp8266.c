#include "stm32f10x.h"
#include "usart1.h"
#include "esp8266.h"
#include "delay.h"
#include <string.h>
#include <stdio.h>

#define  ESP8266_BUF         Usart1RecBuf 
#define  ESP8266_CNT         RxCounter
#define  STM32_RX1BUFF_SIZE  USART1_RXBUFF_SIZE

unsigned short esp8266_cntPre = 0;

//==========================================================
//	函数名称：	ESP8266_Clear
//
//	函数功能：	清空缓存
//
//	入口参数：	无
//
//	返回参数：	无
//
//	说明：	
//==========================================================
void ESP8266_Clear(void)
{
	memset(ESP8266_BUF, 0, sizeof(ESP8266_BUF));
	ESP8266_CNT = 0;
}

//==========================================================
//	函数名称：	ESP8266_WaitRecive
//
//	函数功能：	等待接收完成
//
//	入口参数：	无
//
//	返回参数：	REV_OK-接收完成		REV_WAIT-接收超时未完成
//
//	说明：		循环调用检测是否接收完成
//==========================================================
_Bool ESP8266_WaitRecive(void)
{
	if(ESP8266_CNT == 0) 							//如果接收计数为0 则说明没有处于接收数据中，所以直接跳出，结束函数
		return REV_WAIT;
		
	if(ESP8266_CNT == esp8266_cntPre)				//如果上一次的值和这次相同，则说明接收完毕
	{
		ESP8266_CNT = 0;							//清0接收计数
			
		return REV_OK;								//返回接收完成标志
	}
		
	esp8266_cntPre = ESP8266_CNT;					//置为相同
	
	return REV_WAIT;								//返回接收未完成标志
}

//==========================================================
//	函数名称：	ESP8266_SendCmd
//
//	函数功能：	发送命令
//
//	入口参数：	cmd：命令
//				res：需要检查的返回指令
//
//	返回参数：	0-成功	1-失败
//
//	说明：		
//==========================================================
_Bool ESP8266_SendCmd(char *cmd, char *res, u16 time)
{	
  uart1_send((unsigned char *)cmd,strlen((const char *)cmd));
	
	while(time--)
	{
		if(ESP8266_WaitRecive() == REV_OK)							//如果收到数据
		{
			if(strstr((const char *)ESP8266_BUF, res) != NULL)		//如果检索到关键词
			{
				ESP8266_Clear();									//清空缓存
				
				return 0;
			}
		}
		
		delay_ms(1);
	}
	
	return 1;

}

//==========================================================
//	函数名称：	ESP8266_SendData
//
//	函数功能：	发送数据
//
//	入口参数：	data：数据
//				len：长度
//
//	返回参数：	无
//
//	说明：		
//==========================================================
void ESP8266_SendData(unsigned char *data, unsigned short len)
{

	char cmdBuf[32];
	
	ESP8266_Clear();								//清空接收缓存
	sprintf(cmdBuf, "AT+CIPSEND=0,%d\r\n", len);		//发送命令
	if(!ESP8266_SendCmd(cmdBuf, ">", 200))				//收到‘>’时可以发送数据
	{
			uart1_send(data , len);         //发送设备连接请求数据
	}
}

//==========================================================
//	函数名称：	ESP8266_GetIPD
//
//	函数功能：	获取平台返回的数据
//
//	入口参数：	等待的时间(乘以10ms)
//
//	返回参数：	平台返回的原始数据
//
//	说明：		不同网络设备返回的格式不同，需要去调试
//				如ESP8266的返回格式为	"+IPD,x:yyy"	x代表数据长度，yyy是数据内容
//==========================================================
unsigned char *ESP8266_GetIPD(unsigned short timeOut)
{

	char *ptrIPD  = NULL;
	char *CONNECT = NULL;
	
	do
	{
		if(ESP8266_WaitRecive() == REV_OK)								//如果接收完成
		{
			ptrIPD = strstr((char *)ESP8266_BUF, "IPD,");				//搜索“IPD”头
			if(ptrIPD == NULL)											//如果没找到，可能是IPD头的延迟，还是需要等待一会，但不会超过设定的时间
			{
				//printf("\"IPD\" not found\r\n");
			}
			else
			{
				ptrIPD = strchr(ptrIPD, ':');							//找到':'
				if(ptrIPD != NULL)
				{
					ptrIPD++;
					return (unsigned char *)(ptrIPD);
				}
				else
					return NULL;
				
			}
			CONNECT = strstr((char *)ESP8266_BUF, "0,CONNECT");	
			if(CONNECT != NULL)
			{
					CONNECT++;
					return (unsigned char *)(CONNECT);
			}
		}
		delay_ms(5);													//延时等待
	} while(timeOut--);
	
	return NULL;														//超时还未找到，返回空指针

}

//==========================================================
//	函数名称：	ESP8266_Init
//
//	函数功能：	初始化ESP8266
//
//	入口参数：	无
//
//	返回参数：	无
//
//	说明：		
//==========================================================
void ESP8266_Init(void)
{
	uart1_Init(115200);   //串口初始化
	
	ESP8266_Clear();
//	while(ESP8266_SendCmd("AT\r\n\r", "OK", 200))         //测试
//	delay_ms(500);
//	
//	while(ESP8266_SendCmd("AT+CWMODE=2\r\n", "OK", 200))  //服务器搭建在WIFI模块上
//	delay_ms(500);
//	
//	while(ESP8266_SendCmd("AT+CWSAP=\"ESP8266_WIFI\",\"12345678\",5,3\r\n", "OK", 200)) //设置显示名称:ESP8266_WIFI,密码:12345678
//	delay_ms(500);
//	
//	while(ESP8266_SendCmd("AT+CIPMUX=1\r\n", "OK", 200))  //启动多连接，建立服务器都需要配置
//	delay_ms(500);
//	
//	while(ESP8266_SendCmd("AT+CIPSERVER=1,8080\r\n", "OK", 200))  //建立服务器
//	delay_ms(500);
}


//*************************云平台代码***********************//
char device_data[128];
uint8_t SmartConfig = 0;							//配网标志位
uint8_t wifi_status;									//wifi状态标志位
uint16_t time_flag_1ms;

char *ESP8266_GetRece(void)
{
	return ESP8266_BUF;
}
//==========================================================
//	函数名称：	Esp8266LinkloTExplorer
//	函数功能：	连接阿里云，并且订阅主题
//	入口参数：	无
//	返回参数：	无
//==========================================================
void ESP8266LinkloT(void)
{
  char send_buf[512];
  //发送客户端ID、用户名、密码
  sprintf(send_buf,"AT+MQTTUSERCFG=0,1,\"NULL\",\"%s&%s\",\"%s\",0,0,\"\"\r\n",DeviceName,ProductKey,Password);			
	ESP8266_SendCmd(send_buf, "OK",200);
	delay_ms(500);
  //发送客户端ID
  sprintf(send_buf,"AT+MQTTCLIENTID=0,\"%s\"\r\n",ClientId);			
	ESP8266_SendCmd(send_buf, "OK",200);
	delay_ms(500);
  //连接MQTT阿里云服务器
	sprintf(send_buf,"AT+MQTTCONN=0,\"%s\",%s,1\r\n",mqttHostUrl,port);																			 
	ESP8266_SendCmd(send_buf, "OK",200);
		delay_ms(500);
	//订阅主题
	sprintf(send_buf,"AT+MQTTSUB=0,\"/sys/%s/%s/thing/service/property/set\",1\r\n",ProductKey,DeviceName);	
	ESP8266_SendCmd(send_buf, "OK",200);
		delay_ms(500);
	
}

//==========================================================
//	函数名称：	ESP8266_Status
//	函数功能：	ESP8266状态判断
//	入口参数：	无
//	返回参数：	0-连接状态     1-断开状态
//AT+CIPSTA_CUR?
//+CIPSTA_CUR:ip:"192.168.2.12"
//+CIPSTA_CUR:gateway:"192.168.2.1"
//+CIPSTA_CUR:netmask:"255.255.255.0"
//OK
//==========================================================
_Bool ESP8266_Status(void)
{
	unsigned int timeOut;
	char *Status;
	/* IP查询 */
	uart1_send((unsigned char *)"AT+CIPSTA?\r\n", strlen((const char *)"AT+CIPSTA?\r\n"));
	timeOut = 50;
	while(timeOut--)
	{
		if(ESP8266_WaitRecive() == REV_OK)							//如果收到数据
		{
			if(strstr((const char *)ESP8266_BUF, "OK") != NULL)		//如果检索到关键词
			{
				Status = strstr((const char *)ESP8266_BUF, "ip:");
				if(*(Status+4)!='0')
				{
					ESP8266_Clear();									//清空缓存
					return 0;
				}
				else
				{
					ESP8266_Clear();									//清空缓存
					return 1;
				}
			}
		}
		delay_ms(10);
	}
	ESP8266_Clear();
	return 1;
}

//==========================================================
//	函数名称：	ESP8266_Get_Time
//	函数功能：	获取网络时间
//	入口参数：	无
//	返回参数：	无
//==========================================================
Time_Get ESP8266_Get_Time(void)
{
	unsigned short timeOut;
	char *Start;
	Time_Get Time;
	
	uart1_send((unsigned char *)"AT+CIPSNTPTIME?\r\n", strlen((const char *)"AT+CIPSNTPTIME?\r\n"));
	timeOut = 2000;
	while(timeOut--)
	{
		if(ESP8266_WaitRecive() == REV_OK)							//如果收到数据
		{
			if(strstr((const char *)ESP8266_BUF, "OK") != NULL)		//如果检索到关键词
			{
				Start = strstr((const char *)ESP8266_BUF, "TIME:") + 5;
				
				if(strstr(Start, "Mon") != NULL)				//星期获取
					Time.week = 1;
				else if(strstr(Start, "Tue") != NULL)
					Time.week = 2;
				else if(strstr(Start, "Wed") != NULL)
					Time.week = 3;
				else if(strstr(Start, "Thu") != NULL)
					Time.week = 4;
				else if(strstr(Start, "Fri") != NULL)
					Time.week = 5;
				else if(strstr(Start, "Sat") != NULL)
					Time.week = 6;
				else if(strstr(Start, "Sun") != NULL)
					Time.week = 7;
				
				if(strstr(Start, "Jan") != NULL)				//月份获取
					Time.month = 1;
				else if(strstr(Start, "Feb") != NULL)
					Time.month = 2;
				else if(strstr(Start, "Mar") != NULL)
					Time.month = 3;
				else if(strstr(Start, "Apr") != NULL)
					Time.month = 4;
				else if(strstr(Start, "May") != NULL)
					Time.month = 5;
				else if(strstr(Start, "Jun") != NULL)
					Time.month = 6;
				else if(strstr(Start, "Jul") != NULL)
					Time.month = 7;
				else if(strstr(Start, "Aug") != NULL)
					Time.month = 8;
				else if(strstr(Start, "Sep") != NULL)
					Time.month = 9;
				else if(strstr(Start, "Oct") != NULL)
					Time.month = 10;
				else if(strstr(Start, "Nov") != NULL)
					Time.month = 11;
				else if(strstr(Start, "Dec") != NULL)
					Time.month = 12;
				
				Start = strstr(Start, " ") + 1;		//月份首地址
				Start = strstr(Start, " ") + 1;		//日期首地址
				if(*Start==' ')
				{
				  Start = strstr(Start, " ") + 1;		//日期首地址
				  Time.day = (*Start-'0');		//获取日期
				}
				else
				{
				  Time.day = (*Start-'0')*10 + (*(Start+1)-'0');		//获取日期
				}
				Start = strstr(Start, " ") + 1;		//小时首地址
				Time.hour = (*Start-'0')*10 + (*(Start+1)-'0');		//获取小时
				Start = strstr(Start, ":") + 1;		//分钟首地址
				Time.minute = (*Start-'0')*10 + (*(Start+1)-'0');		//获取分钟
				Start = strstr(Start, ":") + 1;		//秒首地址
				Time.second = (*Start-'0')*10 + (*(Start+1)-'0');		//获取秒
				Start = strstr(Start, " ") + 1;		//年首地址
				Time.year = (*Start-'0')*1000 + (*(Start+1)-'0')*100 + (*(Start+2)-'0')*10 + (*(Start+3)-'0');//获取年
				
				ESP8266_Clear();									//清空缓存
				break;
			}
		}
		
		delay_ms(10);
	}
	return Time;
}

