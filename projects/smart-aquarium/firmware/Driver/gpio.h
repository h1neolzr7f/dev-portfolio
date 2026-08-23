#ifndef __GPIO_H
#define __GPIO_H	 
#include "sys.h"

#define KEY1 PBin(12)
#define KEY2 PBin(13)
#define KEY3 PBin(14)

#define BEEP   PBout(5)

#define relay2   PBout(3)
#define relay3   PBout(4)

#define hot   PBout(10)

#define led   PAout(15)

void KEY_GPIO_Init(void);//Òý½Å³õÊ¼»¯

#endif
