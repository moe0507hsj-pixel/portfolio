using System;
using System.Collections.Generic;
using System.Text;

namespace Shift_App
{
    internal class Staff
    {
        public string Name { get; set; }
        public string StaffNo { get; set; }
        public string StartTime { get; set; }

        public string FinishTime { get; set; }
            public void Print()
        {
            Console.WriteLine($"社員番号 {StaffNo}/{Name}/開始時間{StartTime}/終了時間{FinishTime}");
        }
    }
}
