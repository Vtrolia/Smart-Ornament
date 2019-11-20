#include <Adafruit_GFX.h>
#include <Fonts/FreeSerif12pt7b.h>
#include <MCUFRIEND_kbv.h>
#include <SoftwareSerial.h>

MCUFRIEND_kbv tft;
String text;

unsigned int colors[] = {0xFFE0, 0xF800, 0x07E0, 0xFFFF};
int color_switch = 0;

void setup()
{
  tft.reset();
  uint16_t id = tft.readID();
  tft.begin(id);
  tft.setRotation(1);
  tft.fillScreen(0x0000);
  tft.setTextColor(0xFFFF, 0x0000);
  tft.setTextSize(16);
  tft.setTextWrap(false);
  tft.setCursor(0, 0);

  Serial.begin(9600);
  text = "Merry  Christmas!           ";
}

void loop()
{
  if (Serial.available())
  {
    text = Serial.readString();
    text += "     ";
  }
  const int width = tft.width();
  const int height = tft.height();
  const int len = text.length();
  for (int offset = 0; offset < len; offset++)
  {
    tft.setTextColor(colors[color_switch]);
    color_switch = (color_switch + 1) % 4;
    tft.fillScreen(0x0000);
    String t = "";
    for (int i = 0; i < width; i++)
    {
      t += text.charAt((offset + i) % len);
    }
    tft.setCursor(0, height / 4 + 10);
    tft.print(t);
  }

}
