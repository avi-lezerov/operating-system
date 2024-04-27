import subprocess
import statistics
import openpyxl

num_iterations = 1
parameter_values = [1, 2, 4, 8, 16, 32, 64, 128]

def single_run(parameter_value):
    command = f"time ./a.out top128.txt {parameter_value}"
    real_times = []
    user_times = []
    sys_times = []
    for i in range(num_iterations):
        output = subprocess.run(command, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, universal_newlines=True)
        stderr_lines = output.stderr.strip().split("\n")
        time_line = stderr_lines[0]
        user_time = float(time_line.split()[0][:-4])
        sys_time = float(time_line.split()[1][:-6])
        real_time = float(time_line.split()[2][2:-7])
        real_times.append(real_time)
        user_times.append(user_time)
        sys_times.append(sys_time)
    avg_real_time = statistics.mean(real_times)
    avg_user_time = statistics.mean(user_times)
    avg_sys_time = statistics.mean(sys_times)
    return [
        parameter_value,
        f"{avg_real_time:.4f}",
        f"{avg_user_time:.4f}",
        f"{avg_sys_time:.4f}"
    ]

def main():
    workbook = openpyxl.Workbook()
    worksheet = workbook.active
    results = []
    for value in parameter_values:
        result = single_run(value)
        results.append(result)
    headers = ["Parameter Value", "Real", "User", "System"]
    worksheet.append(headers)
    for row in results:
        worksheet.append(row)
    workbook.save("results.xlsx")

if __name__ == "__main__":
    main()