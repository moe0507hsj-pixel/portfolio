// See https://aka.ms/new-console-template for more information

using Shift_App;
using System;
using System.Collections.Generic;
using System.Xml.Linq;

var staffList = new List<Staff>
{
    new Staff  { StaffNo = "01", Name = "山田", StartTime = "08:00", FinishTime= "18:00"},
    new Staff  { StaffNo = "02", Name = "知念", StartTime = "12:00" , FinishTime= "25:00" }
};

foreach(var staff in staffList)
{
    staff.Print();
}
    
Console.WriteLine("Enterキーで終了します");
Console.WriteLine();


