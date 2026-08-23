#include "sys.h"
#include "delay.h"
#include "gpio.h"
#include "OLED_I2C.h"
#include "ds18b20.h"
#include "usart1.h"
#include "esp8266.h"
#include "MOTOR.h"
#include "adc.h"
#include "timer.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define STM32_RX1_BUF       Usart1RecBuf 
#define STM32_Rx1Counter    RxCounter
#define STM32_RX1BUFF_SIZE  USART1_RXBUFF_SIZE

u8 mode=0;
u8 shuaxin = 0;  //刷新标志
u8 setFlag = 0;  //设置标志
unsigned char cmd[7] = {0xA0,0x00,0x00,0x00,0x00,0xA0};
float PH  = 0.0;
u16 Ph_min = 300,Ph_max = 900;   //PH下限上限
u16 Turbidity=0;
u16 TurSetMax=1000;  //浊度上限
u8 light=0;
u8 setLightValue=25;
u8 shanshuo=0;
char display[16];

u8 START1 = 0;//喂食计时标志
u8 START2 = 0;//换水计时标志
u8 FlipFlag = 0;//翻转标志
u8 Feeding = 0;//投食标志

short temperature;//温度m,
u8 tempMin = 10,tempMax = 35;//温度下限上限

u8 SendFlag=0;
long Wshi=5,Wfen=0,Wmiao=0;//默认投食时间为5小时一次
long Hshi=8,Hfen=0,Hmiao=0;//默认换水时间为8小时一次
long sec_count1 = 0;//喂食时间计时
long sec_count2 = 0;//换水时间计时
u8 handle = 0xFF;//换水处理标志
u8 RelayTime=0;//继电器开启时间
u8 MotorTime=0;//步进电机开启时间

u8 InitFlag=1;//初始化标志

void Usart1RxBufClear(void) //清除串口接收缓存
{
	  memset(STM32_RX1_BUF, 0, STM32_RX1BUFF_SIZE);//清除缓存
		STM32_Rx1Counter = 0; 
}

void InitDisplay(void)   //初始化显示
{

	  OLED_ShowStr(0, 0, "PH:", 2,0);
	  OLED_ShowStr(72, 0, "GX:", 2,0); 
	  OLED_ShowStr(0, 2, "Tm:", 2,0);
	  OLED_ShowStr(72, 2, "Tu:", 2,0);
	  OLED_ShowStr(0, 4, "WS:", 2,0);
	  OLED_ShowStr(0, 6, "HS:", 2,0);

}
void display_set_time(void)//显示设置喂食时间
{
	if(setFlag<7)
	{
//		Wshi=sec_count1/3600;
//		Wfen=sec_count1%3600/60;
//		Wmiao=sec_count1%3600%60;

//		Hshi=sec_count2/3600;
//		Hfen=sec_count2%3600/60;
//		Hmiao=sec_count2%3600%60;
		
		 OLED_ShowChar(40,4,Wshi/10+0x30,2,setFlag+1-1);
		 OLED_ShowChar(48,4,Wshi%10+0x30,2,setFlag+1-1);
		 OLED_ShowChar(56,4,':',2,0);
		
		 OLED_ShowChar(64,4,Wfen/10+0x30,2,setFlag+1-2);
		 OLED_ShowChar(72,4,Wfen%10+0x30,2,setFlag+1-2);
		 OLED_ShowChar(80,4,':',2,0);
		
		 OLED_ShowChar(88,4,Wmiao/10+0x30,2,setFlag+1-3);
		 OLED_ShowChar(96,4,Wmiao%10+0x30,2,setFlag+1-3);

		
		 OLED_ShowChar(40,6,Hshi/10+0x30,2,setFlag+1-4);
		 OLED_ShowChar(48,6,Hshi%10+0x30,2,setFlag+1-4);
		 OLED_ShowChar(56,6,':',2,0);
		
		 OLED_ShowChar(64,6,Hfen/10+0x30,2,setFlag+1-5);
		 OLED_ShowChar(72,6,Hfen%10+0x30,2,setFlag+1-5);
		 OLED_ShowChar(80,6,':',2,0);
		
		 OLED_ShowChar(88,6,Hmiao/10+0x30,2,setFlag+1-6);
		 OLED_ShowChar(96,6,Hmiao%10+0x30,2,setFlag+1-6);
	}
}
void display_time(void)//显示设置喂食时间
{
	if(setFlag==0)
	{
		 OLED_ShowChar(40,4,sec_count1/3600/10+0x30,2,0);
		 OLED_ShowChar(48,4,sec_count1/3600%10+0x30,2,0);
		 OLED_ShowChar(56,4,':',2,0);
		
		 OLED_ShowChar(64,4,sec_count1%3600/60/10+0x30,2,0);
		 OLED_ShowChar(72,4,sec_count1%3600/60%10+0x30,2,0);
		 OLED_ShowChar(80,4,':',2,0);
		
		 OLED_ShowChar(88,4,sec_count1%3600%60/10+0x30,2,0);
		 OLED_ShowChar(96,4,sec_count1%3600%60%10+0x30,2,0);
		
		
		
		 OLED_ShowChar(40,6,sec_count2/3600/10+0x30,2,0);
		 OLED_ShowChar(48,6,sec_count2/3600%10+0x30,2,0);
		 OLED_ShowChar(56,6,':',2,0);
		
		 OLED_ShowChar(64,6,sec_count2%3600/60/10+0x30,2,0);
		 OLED_ShowChar(72,6,sec_count2%3600/60%10+0x30,2,0);
		 OLED_ShowChar(80,6,':',2,0);
		
		 OLED_ShowChar(88,6,sec_count2%3600%60/10+0x30,2,0);
		 OLED_ShowChar(96,6,sec_count2%3600%60%10+0x30,2,0);

	}
}

void displaySetValue(void)  //显示设置的值
{
	  if(setFlag == 7 ||setFlag == 8)
		{
				sprintf(display,"%5.2f",(float)Ph_min/100); 
				OLED_ShowStr(40, 4,(u8 *)display, 2,setFlag+1-7);
			
				sprintf(display,"%5.2f",(float)Ph_max/100); 
				OLED_ShowStr(40, 6,(u8 *)display, 2,setFlag+1-8);
		}
		if(setFlag == 9 || setFlag == 10)
		{
				sprintf(display,"%02d",tempMin); 
				OLED_ShowStr(48, 4,(u8 *)display, 2,setFlag+1-9);
			
			  sprintf(display,"%02d",tempMax); 
				OLED_ShowStr(48, 6,(u8 *)display, 2,setFlag+1-10);
		}
		if(setFlag == 11)
		{
				sprintf(display,"%04d",TurSetMax); 
				OLED_ShowStr(40, 4,(u8 *)display, 2,0);
		}
		if(setFlag == 12)
		{
				sprintf(display,"%02d%%    ",setLightValue); 
				OLED_ShowStr(40, 4,(u8 *)display, 2,0);
		}
}

void keyscan(void)   //按键扫描
{
	 unsigned char i=0;
	
	 if(KEY1 == 0) //设置键
	 {
			delay_ms(20);
		  if(KEY1 == 0)
			{
					while(KEY1 == 0);
				  BEEP=0;
				  setFlag ++;
				  if(setFlag == 7)
					{
							OLED_CLS();    //清屏
						  for(i=0;i<2;i++)OLED_ShowCN(i*16+32,0,i+8,0);//显示中文：设置
						  OLED_ShowStr(62, 0, " PH", 2,0);
						  for(i=0;i<2;i++)OLED_ShowCN(i*16,4,i+4,0);//显示中文：下限
							for(i=0;i<2;i++)OLED_ShowCN(i*16,6,i+6,0);//显示中文：上限
						  OLED_ShowChar(32,4,':',2,0);
						  OLED_ShowChar(32,6,':',2,0);
						  
					}
					if(setFlag == 9)
					{
						for(i=0;i<2;i++)OLED_ShowCN(i*16+64,0,i+0,0);//显示中文：温度
						OLED_ShowStr(32, 4, ":      ", 2,0);
						OLED_ShowStr(32, 6, ":      ", 2,0);

						OLED_ShowCentigrade(64, 4);    //℃
						OLED_ShowCentigrade(64, 6);    //℃
						  
					}
					if(setFlag == 11)
					{
							for(i=0;i<2;i++)OLED_ShowCN(i*16+64,0,i+2,0);//显示中文：浊度
							OLED_ShowStr(0, 4, "             ", 2,0);
							OLED_ShowStr(0, 6, "             ", 2,0);
						  OLED_ShowStr(72, 4, "NTU", 2,0);
					}
					if(setFlag == 12)
					{
							for(i = 0;i < 4;i ++)OLED_ShowCN(i*16+32,0,i+8,0);//显示中文：设置光照
						  OLED_ShowChar(80,4,' ',2,0); 
					}
					
					if(setFlag >= 13)
					{
						  setFlag = 0;
						  InitFlag=1;
							OLED_CLS();    //清屏
						  InitDisplay();
						  
					}
					display_set_time();
					displaySetValue();
			}
	 }
	 if(KEY2 == 0) //加键
	 {
			delay_ms(20);
		  if(KEY2 == 0)
			{
				while(KEY2 == 0);
				  if(setFlag == 0 && sec_count1!=0)//喂食时间计时
					{
						 START1=!START1;
						 if(START1==0)
						 {
								sec_count1 = Wshi*3600+Wfen*60+Wmiao;//时间重新赋值
						 } 
					}
					if(setFlag == 1)
					{
						Wshi++;
						if(Wshi==100)Wshi=0;//最大到99小时
			
					}
					if(setFlag == 2)
					{
						Wfen++;
						if(Wfen==60)Wfen=0;
				
					}
					if(setFlag == 3)
					{
						Wmiao++;
						if(Wmiao==60)Wmiao=0;
					
					}
					if(setFlag == 4)
					{
						Hshi++;
						if(Hshi==100)Hshi=0;
					
					}
					if(setFlag == 5)
					{
						Hfen++;
						if(Hfen==60)Hfen=0;
					
					}
					if(setFlag == 6)
					{
						Hmiao++;
						if(Hmiao==60)Hmiao=0;
			
					}
					display_set_time();
					
					if(setFlag == 7)
					{
						  if(Ph_max-Ph_min > 10)Ph_min+=10;
					}
					if(setFlag == 8)
					{
							if(Ph_max < 1400)Ph_max+=10;
					}
					if(setFlag == 9)
					{
							if(tempMin<tempMax)tempMin++;
					}
					if(setFlag == 10)
					{
							if(tempMax<99)tempMax++;
					}
					if(setFlag == 11)
					{
						  if(TurSetMax<3000)TurSetMax+=10;
					}
					if(setFlag == 12)
					{
						  if(setLightValue<99)setLightValue++;
					}
					displaySetValue();   //显示没有设置值
			}
	 }
	 if(KEY3 == 0) //减键
	 {
			delay_ms(20);
		  if(KEY3 == 0)
			{
				  while(KEY3 == 0);
					if(setFlag == 0 && sec_count2!=0)//换水时间计时
					{
						 START2=!START2;
						 if(START2==0)
						 {
								sec_count2 = Hshi*3600+Hfen*60+Hmiao;
						 } 
					}
					if(setFlag == 1)
					{
						if(Wshi==0)Wshi=100;
						Wshi--;
					
					}
					if(setFlag == 2)
					{
						if(Wfen==0)Wfen=60;
						Wfen--;
					
					}
					if(setFlag == 3)
					{
						if(Wmiao==0)Wmiao=60;
						Wmiao--;
						
					}
					if(setFlag == 4)
					{
						if(Hshi==0)Hshi=100;
						Hshi--;
					
					}
					if(setFlag == 5)
					{
						if(Hfen==0)Hfen=60;
						Hfen--;
					
					}
					if(setFlag == 6)
					{
						if(Hmiao==0)Hmiao=60;
						Hmiao--;
					
					}

					display_set_time();
					
					if(setFlag == 7)
					{
						  if(Ph_min >= 10)Ph_min-=10;
					}
					if(setFlag == 8)
					{
							if(Ph_max-Ph_min > 10)Ph_max-=10;
					}
					if(setFlag == 9)
					{
							if(tempMin>0)tempMin--;
					}
					if(setFlag == 10)
					{
							if(tempMax>tempMin)tempMax--;
					}
					if(setFlag == 11)
					{
						  if(TurSetMax>=10)TurSetMax-=10;
					}
					if(setFlag == 12)
					{
						  if(setLightValue>0)setLightValue--;
					}
					displaySetValue();   //显示没有设置值
			}
	 }
}

unsigned long int avgValue;	//Store the average value of the sensor feedback 
#define RATIO  4.51/4.08
void Get_PH(void)    //获取PH
{
	  u16 buf[10];//buffer for read analog
	  u8 i,j;	
	  float phValue=0.0;
	
		for(i=0;i<10;i++)	//Get 10 sample value from the sensor for smooth the value
		{
			buf[i]=Get_Adc_Average(ADC_Channel_0,1); 
		}
		for(i=0;i<9;i++)	//sort the analog from small to large
		{
				for(j=i+1;j<10;j++)
				{
						if(buf[i]>buf[j])
						{
							int temp=buf[i];
							buf[i]=buf[j]; 
							buf[j]=temp;
						}
				}
		}
		avgValue=0;
		for(i=1;i<9;i++)	//take the average value of 6 center sample 
		  avgValue+=buf[i];
    phValue=((float)avgValue*3.3/4096/8); //convert the analog into millivolt
		PH=(phValue*(-5.290))+23.053;	//convert the millivolt into pH value 
		
		if(PH<0)PH=0;
		if(PH>14.0)PH=14.0;
		PH=PH*100;
	
		/*超限的时候闪烁显示*/
		if((PH<=Ph_min || PH>=Ph_max) && shanshuo)
		{
				OLED_ShowStr(24, 0,"     ", 2,0);
		}
		else
		{
				sprintf(display,"%0.2f ",(float)PH/100); 
				OLED_ShowStr(24, 0,(u8 *)display, 2,0);
		}
}

void Get_Turbidity(void)   //获取浑浊度
{
		float T;
	  u16 adcx = 0;
	
	  adcx = Get_Adc_Average(ADC_Channel_9,1);//读取AD值
		T = adcx;
		T = T*(3.3/4096)+1.72;
		if(T < 2.5)
		{
				T = 3000;	
		}
		else 
		{
				T = (-1120.4*T*T+5742.3*T-4352.9);	//Tul是AD值
		}
		if(T < 0)
		{
			T = 0;
		}
		Turbidity = (u16)T;
		
		if(Turbidity > 3000)Turbidity = 3000;
		
		/*超限的时候闪烁显示*/
		if((Turbidity>=TurSetMax) && shanshuo)
		{
				OLED_ShowStr(96, 2,"    ", 2,0);
		}
		else
		{
				sprintf(display,"%d",Turbidity); 
				OLED_ShowStr(96, 2,(u8 *)display, 2,0);
		}
}
void Get_Light(void)    //获取可燃气体浓度
{
		u16 test_adc=0;
	
	  /////////////获取光线值
	  test_adc = Get_Adc_Average(ADC_Channel_1,1);//读取通道9的5次AD平均值
		light = test_adc*99/4096;//转换成0-99百分比
		light = light >= 99? 99: light;//最大只能到百分之99
	
	  if(light<=setLightValue && shanshuo)
		{
				OLED_ShowStr(96, 0,"  ", 2,0);
		}
		else
		{
				sprintf(display,"%02d%%",light);
				OLED_ShowStr(96, 0, (u8*)display, 2,0);//显示温度
		}
}
void Get_Temp(void)
{
	  temperature = ReadTemperature();//读取温度
		/*超限的时候闪烁显示*/
		if((temperature>=tempMax || temperature<=tempMin) && shanshuo)
		{
				OLED_ShowStr(24, 2,"    ", 2,0);
		}
		else
		{
				sprintf(display,"%02d",temperature); 
				OLED_ShowStr(24, 2,(u8 *)display, 2,0);
				OLED_ShowCentigrade(40, 2);    //℃
		}
}
void display_mode(void)
{
	if(mode==0)
	  OLED_ShowStr(112, 5,"A", 2,1);
	else
		OLED_ShowStr(112, 5,"M", 2,1);
}



//==========================================================
//	函数名称：	Ali_MQTT_Publish
//	函数功能：	向阿里云发布消息
//	入口参数：	无
//	返回参数：	无
//==========================================================
void Ali_MQTT_Publish(void)
{
	char buf[200];
	char txt[512];
	memset(txt, 0, sizeof txt);
	
	sprintf(buf, "AT+MQTTPUB=0,\"/sys/%s/%s/thing/event/property/post\",",ProductKey,DeviceName);
	strcat(txt, buf);
	strcat(txt,"\"{\\\"method\\\":\\\"thing.service.property.set\\\"\\,\\\"id\\\":\\\"2012934115\\\"\\,\\\"params\\\":{");
  
	
	/* 数据点整合 */
  sprintf(buf,"\\\"temp\\\":%d\\,", (int)temperature);
	strcat(txt, buf);
	
	sprintf(buf,"\\\"tu\\\":%d\\,",Turbidity );
	strcat(txt, buf);
	
	if(relay2==1)
		sprintf(buf,"\\\"motor_out\\\":%d\\,", 1);
	else
		sprintf(buf,"\\\"motor_out\\\":%d\\,", 0);
	strcat(txt, buf);
	
	if(relay3==1)
		sprintf(buf,"\\\"motor_in\\\":%d\\", 1);
	else
		sprintf(buf,"\\\"motor_in\\\":%d\\", 0);
	strcat(txt, buf);

	strcat(txt, "}\\,\\\"version\\\":\\\"1.0.0\\\"}\",1,0\r\n");
	//发送数据
	ESP8266_SendCmd(txt, "",300);
}
void Ali_MQTT_Publish1(void)
{
	char buf[200];
	char txt[512];
	memset(txt, 0, sizeof txt);
	
	sprintf(buf, "AT+MQTTPUB=0,\"/sys/%s/%s/thing/event/property/post\",",ProductKey,DeviceName);
	strcat(txt, buf);
	strcat(txt,"\"{\\\"method\\\":\\\"thing.service.property.set\\\"\\,\\\"id\\\":\\\"2012934115\\\"\\,\\\"params\\\":{");
  
	
	/* 数据点整合 */
	if(led==1)
		sprintf(buf,"\\\"led\\\":%d\\,", 1);
	else
		sprintf(buf,"\\\"led\\\":%d\\,", 0);
	strcat(txt, buf);
	
	sprintf(buf,"\\\"feed\\\":%d\\,", Feeding);
	strcat(txt, buf);
	
	if(hot==0)
	  sprintf(buf,"\\\"hot\\\":%d\\,", 1);
	else
		sprintf(buf,"\\\"hot\\\":%d\\,", 0);
	strcat(txt, buf);
	
	
	sprintf(buf,"\\\"mode\\\":%d\\", mode);
	strcat(txt, buf);
	
	strcat(txt, "}\\,\\\"version\\\":\\\"1.0.0\\\"}\",1,0\r\n");
	//发送数据
	ESP8266_SendCmd(txt, "",300);
}

void Ali_MQTT_Publish2(void)
{
	char buf[200];
	char txt[512];
	memset(txt, 0, sizeof txt);
	
	sprintf(buf, "AT+MQTTPUB=0,\"/sys/%s/%s/thing/event/property/post\",",ProductKey,DeviceName);
	strcat(txt, buf);
	strcat(txt,"\"{\\\"method\\\":\\\"thing.service.property.set\\\"\\,\\\"id\\\":\\\"2012934115\\\"\\,\\\"params\\\":{");
  
	
	/* 数据点整合 */
	sprintf(buf,"\\\"light\\\":%d\\,", light);
	strcat(txt, buf);
	
	sprintf(buf,"\\\"ph\\\":%.2f\\", (float)PH/100);
	strcat(txt, buf);
	
	strcat(txt, "}\\,\\\"version\\\":\\\"1.0.0\\\"}\",1,0\r\n");
	//发送数据
	ESP8266_SendCmd(txt, "",300);
}

//==========================================================
//	函数名称：	Ali_MQTT_Recevie
//	函数功能：	接收腾讯云下发的消息
//	入口参数：	无
//	返回参数：	无
//==========================================================

void Ali_MQTT_Recevie(void)
{
		char *dataPtr = NULL;
		char *str1=0,i;
	  int  setValue=0;
	  char setvalue[6]={0};
	
	  dataPtr = ESP8266_GetRece();   //接收数据
		
		if(dataPtr != NULL)
		{
				if(strstr((char *)dataPtr,"mode")!=NULL)
				{
					  BEEP = 1;
						delay_ms(80);
						BEEP = 0;
					  str1 = strstr((char *)dataPtr,"mode");
						
						while(*str1 < '0' || *str1 > '9')    //判断是不是0到9有效数字
						{
								str1 = str1 + 1;
								delay_ms(10);
						}
						i = 0;
						while(*str1 >= '0' && *str1 <= '9')        //判断是不是0到9有效数字
						{
								setvalue[i] = *str1;
								i ++; str1 ++;
								if(*str1 == ',')break;            //换行符，直接退出while循环
								delay_ms(10);
						}
						setvalue[i] = '\0';            //加上结尾符
						setValue = atoi(setvalue);
						if(setValue==0)
						{
								mode=0;
						}else
						{
								mode=1;
						}
						if(setFlag == 0)
						{
								display_mode();
						}
				}
				if(mode==1) //在手动模式下，并且有水才能控制
				{
						if(strstr((char *)dataPtr,"ctr_water")!=NULL)  //手动换水
						{
								str1 = strstr((char *)dataPtr,"ctr_water");
						
								while(*str1 < '0' || *str1 > '9')    //判断是不是0到9有效数字
								{
										str1 = str1 + 1;
										delay_ms(10);
								}
								i = 0;
								while(*str1 >= '0' && *str1 <= '9')        //判断是不是0到9有效数字
								{
										setvalue[i] = *str1;
										i ++; str1 ++;
										if(*str1 == ',')break;            //换行符，直接退出while循环
										delay_ms(10);
								}
								setvalue[i] = '\0';            //加上结尾符
								setValue = atoi(setvalue);
								if(setValue==0)
								{
										relay2 = 0;//出水继电器开启
										relay3 = 0;//进水继电器关闭
								}else
								{
										relay2 = 1;//出水继电器开启
										relay3 = 1;//进水继电器关闭
								}

								BEEP = 1;
								delay_ms(80);
								BEEP = 0;
						}
						if(strstr((char *)dataPtr,"ctr_led")!=NULL)  //手动增氧
						{
							  str1 = strstr((char *)dataPtr,"ctr_led");
						
								while(*str1 < '0' || *str1 > '9')    //判断是不是0到9有效数字
								{
										str1 = str1 + 1;
										delay_ms(10);
								}
								i = 0;
								while(*str1 >= '0' && *str1 <= '9')        //判断是不是0到9有效数字
								{
										setvalue[i] = *str1;
										i ++; str1 ++;
										if(*str1 == ',')break;            //换行符，直接退出while循环
										delay_ms(10);
								}
								setvalue[i] = '\0';            //加上结尾符
								setValue = atoi(setvalue);
								if(setValue==0)
								{
										led=0;
								}else
								{
										led=1;
								}
								BEEP = 1;
								delay_ms(80);
								BEEP = 0;
						}
						if(strstr((char *)dataPtr,"ctr_hot")!=NULL)  //手动加热
						{
							  str1 = strstr((char *)dataPtr,"ctr_hot");
						
								while(*str1 < '0' || *str1 > '9')    //判断是不是0到9有效数字
								{
										str1 = str1 + 1;
										delay_ms(10);
								}
								i = 0;
								while(*str1 >= '0' && *str1 <= '9')        //判断是不是0到9有效数字
								{
										setvalue[i] = *str1;
										i ++; str1 ++;
										if(*str1 == ',')break;            //换行符，直接退出while循环
										delay_ms(10);
								}
								setvalue[i] = '\0';            //加上结尾符
								setValue = atoi(setvalue);
								if(setValue==0)
								{
										hot=0;
								}else
								{
										hot=1;
								}
								BEEP = 1;
								delay_ms(80);
								BEEP = 0;
						}
						if(strstr((char *)dataPtr,"ctr_feed")!=NULL) //手动喂食
						{
								 str1 = strstr((char *)dataPtr,"ctr_feed");
						
								while(*str1 < '0' || *str1 > '9')    //判断是不是0到9有效数字
								{
										str1 = str1 + 1;
										delay_ms(10);
								}
								i = 0;
								while(*str1 >= '0' && *str1 <= '9')        //判断是不是0到9有效数字
								{
										setvalue[i] = *str1;
										i ++; str1 ++;
										if(*str1 == ',')break;            //换行符，直接退出while循环
										delay_ms(10);
								}
								setvalue[i] = '\0';            //加上结尾符
								setValue = atoi(setvalue);
								if(setValue==0)
								{
										
								}else
								{
										Feeding=1;
								}
								BEEP = 1;
								delay_ms(80);
								BEEP = 0;
						}
						
				}
				ESP8266_Clear();									//清空缓存
		}
}

//==========================================================
//	函数名称：	ESP8266_APP
//	函数功能：	主函数调用函数，封装上传以及下发
//	入口参数：	无
//	返回参数：	无
//==========================================================
void  ESP8266_App(void)
{
  if(SmartConfig == 1)
	{
		if(SendFlag == 1)							//每3秒上报一次数据
		{
			SendFlag = 0;
			if(ESP8266_Status() == 0)		//WiFi连接状态
			{
				//WIFI_LED(1);
				delay_ms(10);
				if(wifi_status == 0)
				{
					wifi_status = 1;
					ESP8266LinkloT();
				}
				/* 上报数据 */
				Ali_MQTT_Publish();
				Ali_MQTT_Publish1();
				Ali_MQTT_Publish2();
			}
			else
			{
				//WIFI_LED(0);
				if(wifi_status == 1)		//WiFi未连接-显示断开
				{
					wifi_status = 0;
				}
			}
		}
	}
	Ali_MQTT_Recevie();
}

int main(void)
{
		delay_init();	           //延时函数初始化	 
    NVIC_Configuration();	   //中断优先级配置
	  I2C_Configuration();     //IIC初始化
	  delay_ms(200); 
	  Adc_Init();		  	      	//ADC初始化	
	  OLED_Init();             //OLED液晶初始化
	  OLED_CLS();              //清屏
	  OLED_ShowStr(0, 2,"   Loading...   ", 2,0);
	  ESP8266_Init();
	  OLED_CLS();              //清屏
	  InitDisplay();
		KEY_GPIO_Init();        //按键引脚初始化
	  DS18B20_GPIO_Init();
	  MOTOR_GPIO_Init();
	  DS18B20_Init();         //初始化显示

		while(1)
		{ 
			
			   keyscan();  //按键扫描
				 if(InitFlag==1)//初始化
				 {
						InitFlag = 0;
						START1=START2=0;
						setFlag = 0;
						delay_ms(5);
						TIM2_Init(499,7199);     //定时器初始化，定时50ms
						//Tout = ((arr+1)*(psc+1))/Tclk ;  
						//Tclk:定时器输入频率(单位MHZ)
						//Tout:定时器溢出时间(单位us)
						sec_count1 = Wshi*3600+Wfen*60+Wmiao;//投食时间赋值
						sec_count2 = Hshi*3600+Hfen*60+Hmiao;//换水时间赋值
				 }
			   display_time();
				 
 
			   if(shuaxin==1 && !setFlag)   //延时一段时间读取
				 {
						shuaxin = 0;
					 
					  shanshuo=!shanshuo;
					 
					  Get_PH() ;      //获取PH
					  Get_Light();
					  Get_Temp();
					  Get_Turbidity() ;
						
						if(mode==0)
						{
							 if((PH<=Ph_min || PH>=Ph_max)||(temperature>=tempMax)||(temperature<=tempMin)||(Turbidity>=TurSetMax))BEEP= 1;else BEEP=0;  //超限蜂鸣器报警 
							 
							 if(light<=setLightValue)
							 {
								 led=1;
							 }
							 else
							 {
								 led=0;
							 }
							 if(temperature<=tempMin)
							 {
								 hot=0;
							 }
							 else
							 {
								 hot=1;
							 }
							 if((temperature>= tempMax || Turbidity>=TurSetMax) && START2==0)
							 {
								 relay2 = 1;//出水继电器开启
								 relay3 = 1;//进水继电器关闭
							 }
							 else if (START2==0)
							 {
								 relay2 = 0;//出水继电器开启
								 relay3 = 0;//进水继电器关闭
							 }
						}
					 
					 
					
				 }
				 if(setFlag==0)
				 {
					 display_mode();
					 if(Feeding)//喂食时间到，电机转动
							{
									if(MotorTime<8)MotorCW();//正转
									else if(MotorTime>=8&&MotorTime<=12)MotorStop();//停止
									else if(MotorTime>12)MotorCCW();//反转
							}
							else
							{
									MotorStop();//否则停止
							}
				 }
				ESP8266_App();
				delay_ms(1);
		}
}

void TIM2_IRQHandler(void)//定时器2中断服务程序	 
{ 
	  static u8 time_count = 0,time_count1s=0;
	  static u8 time_count5s=0;
	
		if (TIM_GetITStatus(TIM2, TIM_IT_Update) != RESET) //检查指定的TIM中断发生与否:TIM 中断源 
		{ 
				TIM_ClearITPendingBit(TIM2, TIM_IT_Update); //清除中断标志位  
			  if(time_count++ >= 10)//500毫秒
				{
						time_count = 0;
						FlipFlag = !FlipFlag;
				}
			
				if(time_count1s++ >= 20)//1秒时间到
				{
						time_count1s = 0;
						if(START1)//喂食时间倒计时开始
						{
								if(sec_count1>0)sec_count1--;
								else
								{
										Feeding = 1;
								}
						}
						if(START2)//换水时间倒计时开始
						{
								if(sec_count2>0)sec_count2--;
								else
								{
										if(handle==0xFF)
										{
												handle >>= 1; // 0111 1111 0x7F
												RelayTime=5;
												relay2 = 1;//出水继电器开启
												relay3 = 0;//进水继电器关闭
										}
								}
						}
						if(RelayTime>0)RelayTime--;
						else
						{
								if(handle==0x7F)//出水继电器计时到0
								{
										handle >>= 1; // 0011 1111  0x3F
										RelayTime=5; 
										relay2 =0; //出水继电器关闭
										relay3 = 1;//进水继电器开启
								}
								else
								{
										if(handle==0x3F)
										{
												handle = 0xFF;
												RelayTime=0;
												relay2 =0;
												relay3 = 0;
												sec_count2 = Hshi*3600+Hfen*60+Hmiao;//换水时间重新赋值
										}
								}
						}	
						if(Feeding)
						{
								if(MotorTime<20)MotorTime++;
								else 
								{
										Feeding = 0;
										MotorTime = 0;
										sec_count1 = Wshi*3600+Wfen*60+Wmiao; //喂食时间重新赋值
								}
						}
						
						shuaxin=1;
						
						time_count5s++;
						if(time_count5s>5)
						{
							time_count5s=0;
							SendFlag=1;
						}
						
				}
	  }
}

