import subprocess
import statistics

# Specify the command to run and number of iterations
Buffer_size = 100
command = "time ./a.out -f " + Buffer_size + "/tmp/test.5mb /tmp/test.5mb_dest"
num_iterations = 1

# Run the command multiple times and record execution times
real_times = []
user_times = []
sys_times = []

for i in range(num_iterations):
    output = subprocess.run(command, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, universal_newlines=True)
    stderr_lines = output.stderr.strip().split("\n")
    
    real_time = float(stderr_lines[0].split()[1])
    user_time = float(stderr_lines[1].split()[1])
    sys_time = float(stderr_lines[2].split()[1])
    
    real_times.append(real_time)
    user_times.append(user_time)
    sys_times.append(sys_time)
    
    # print(f"Iteration {i+1}:")
    # print(f"  Real: {real_time:.3f} seconds")
    # print(f"  User: {user_time:.3f} seconds")
    # print(f"  Sys: {sys_time:.3f} seconds")

# Calculate average times and standard deviations
avg_real_time = statistics.mean(real_times)
std_dev_real_time = statistics.stdev(real_times)

avg_user_time = statistics.mean(user_times)
std_dev_user_time = statistics.stdev(user_times)

avg_sys_time = statistics.mean(sys_times)
std_dev_sys_time = statistics.stdev(sys_times)

# print(f"\nAverage times:")
# print(f"  Real: {avg_real_time:.3f} seconds (Standard deviation: {std_dev_real_time:.3f} seconds)")
# print(f"  User: {avg_user_time:.3f} seconds (Standard deviation: {std_dev_user_time:.3f} seconds)")
# print(f"  Sys: {avg_sys_time:.3f} seconds (Standard deviation: {std_dev_sys_time:.3f} seconds)")