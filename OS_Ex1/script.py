import subprocess
import statistics

num_iterations = 100
buffer_size = [100, 200, 400, 800, 1600, 3200, 6400, 12800, 25600, 51200]




def single_run(buffer_size):
    command = "time ./a.out -f "+ str(buffer_size) +" /tmp/test.5mb /tmp/test.5mb_dest"
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

    # Calculate average times
    avg_real_time = statistics.mean(real_times)
    avg_user_time = statistics.mean(user_times)
    avg_sys_time = statistics.mean(sys_times)

    # Calculate standard deviations if there are at least two data points
    std_dev_real_time = statistics.stdev(real_times) if len(real_times) >= 2 else 0
    std_dev_user_time = statistics.stdev(user_times) if len(user_times) >= 2 else 0
    std_dev_sys_time = statistics.stdev(sys_times) if len(sys_times) >= 2 else 0

    print(f"\nAverage times:" +f"Buffer size: {buffer_size} bytes")
    print(f"  Real: {avg_real_time:.4f} seconds (Standard deviation: {std_dev_real_time:.3f} seconds)")
    print(f"  User: {avg_user_time:.4f} seconds (Standard deviation: {std_dev_user_time:.3f} seconds)")
    print(f"  Sys: {avg_sys_time:.4f} seconds (Standard deviation: {std_dev_sys_time:.3f} seconds)")
    print("\n")


def main():
    # for size in buffer_size:
        single_run(51200)
    

if __name__ == "__main__":
    main()