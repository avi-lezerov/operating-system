/*
 * ex1.c
 */

#include <sys/types.h>
#include <sys/stat.h>

#include <fcntl.h>
#include <getopt.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#define MAX_BUFFER_SIZE 65536
#define DESTINATION_FILE_MODE S_IRUSR|S_IWUSR|S_IRGRP|S_IROTH

extern int opterr, optind;

void exit_with_usage(const char *message) {
	fprintf (stderr, "%s\n", message);
	fprintf (stderr, "Usage:\n\tex1 [-f] BUFFER_SIZE SOURCE DEST\n");
	exit(EXIT_FAILURE);
}

/**
 * an helper function to open the files
 * @param source_file the name of the source file
 * @param dest_file the name of the destination file
 * @param force_flag a flag to indicate if the destination file should be overwritten
 * @param source_fd a pointer to the file descriptor of the source file
 * @param dest_fd a pointer to the file descriptor of the destination file
*/
void open_files(const char *source_file, const char *dest_file, int force_flag, int *source_fd, int *dest_fd){
    *source_fd = open(source_file, O_RDONLY);
    if (*source_fd == -1) {
        perror("Unable to open source file for reading");
        exit(EXIT_FAILURE);
    }

    int flags = O_WRONLY | O_CREAT;
    if (force_flag) {
        flags |= O_TRUNC;
    } else {
        flags |= O_EXCL;
    }

    *dest_fd = open(dest_file, flags, DESTINATION_FILE_MODE);
    if (*dest_fd == -1) {
        perror("Unable to open destination file for writing");
        close(*source_fd);
        exit(EXIT_FAILURE);
    }
}

/**
 * an helper function to close the files
 * @param source_fd the file descriptor of the source file
*/
void close_files(int source_fd, int dest_fd){
	if (close(source_fd) == -1) {
			perror("Unable to close source file");
			close(dest_fd);
			exit(EXIT_FAILURE);
	}
	if (close(dest_fd) == -1) {
		perror("Unable to close destination file");
		exit(EXIT_FAILURE);
	}
}
	

void copy_file(const char *source_file, const char *dest_file, int buffer_size, int force_flag) {
	int source_fd, dest_fd;
    ssize_t bytes_read, bytes_written;
	char buffer[buffer_size];

	open_files(source_file, dest_file, force_flag, &source_fd, &dest_fd);
	
	while((bytes_read = read(source_fd, buffer, buffer_size)) > 0) {
		bytes_written = write(dest_fd, buffer, bytes_read);
		if (bytes_written == -1) {
			perror("Unable to write to destination file");
			close_files(source_fd, dest_fd);
			exit(EXIT_FAILURE);
			}
		if (bytes_written != bytes_read) {
            fprintf(stderr, "Unable to write buffer content to destination file\n");
            close_files(source_fd, dest_fd);
            exit(EXIT_FAILURE);
        	}
		}
	
		close_files(source_fd, dest_fd);

		printf("File %s was successfully copied to %s\n",source_file, dest_file);
	}
	
void parse_arguments(
		int argc, char **argv,
		char **source_file, char **dest_file, int *buffer_size, int *force_flag) {
	/*
	 * parses command line arguments and set the arguments required for copy_file
	 */
	int option_character;

	opterr = 0; /* Prevent getopt() from printing an error message to stderr */

	while ((option_character = getopt(argc, argv, "f")) != -1) {
		switch (option_character) {
		case 'f':
			*force_flag = 1;
			break;
		default:  /* '?' */
			exit_with_usage("Unknown option specified");
		}
	}

	if (argc - optind != 3) {
		exit_with_usage("Invalid number of arguments");
	} else {
		*source_file = argv[argc-2];
		*dest_file = argv[argc-1];
		*buffer_size = atoi(argv[argc-3]);

		if (strlen(*source_file) == 0 || strlen(*dest_file) == 0) {
			exit_with_usage("Invalid source / destination file name");
		} else if (*buffer_size < 1 || *buffer_size > MAX_BUFFER_SIZE) {
			exit_with_usage("Invalid buffer size");
		}
	}
}

int main(int argc, char **argv) {
	int force_flag = 0; /* force flag default: false */
	char *source_file = NULL;
	char *dest_file = NULL;
	int buffer_size = MAX_BUFFER_SIZE;

	parse_arguments(argc, argv, &source_file, &dest_file, &buffer_size, &force_flag);

	copy_file(source_file, dest_file, buffer_size, force_flag);

	return EXIT_SUCCESS;
}