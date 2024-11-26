### Distributed Averaging System (DAS) Task

### Introduction

The task is to write an application implementing a **Distributed Averaging System**.

### Application Functionality

The application consists of a single program implementing the **DAS (Distributed Averaging System)** class, launched with the following command:

```bash
java DAS <port> <number>
```

Where:
- `<port>` is the number specifying the UDP port.
- `<number>` is an integer.

The application operates in two modes: **master** and **slave**. The mode is determined automatically upon execution based on the current system state and the provided parameters. Upon execution, the application attempts to create a UDP socket on the port specified by `<port>`. This operation may result in two possible outcomes:

1. **Success in opening the requested port:** The application enters **master mode**.
2. **Failure to open the requested port:** It is assumed this happens only when another instance of the application is already running in master mode on the same machine. In this case, the application switches to **slave mode**.

---

### Master Mode

The application in **master mode** stores the value provided as the `<number>` parameter and enters a loop where it continuously listens for messages on the UDP socket at port `<port>`. Based on the value received in the message, the behavior is as follows:

1. **If the value is neither `0` nor `-1`:**
    - The process prints the value to the console.
    - The value is stored for later use.

2. **If the value is `0`:**
    - The process performs the following actions sequentially:
        1. Calculates the average of all non-zero numbers received since the application started, including the `<number>` parameter.
        2. Prints the calculated average to the console.
        3. Sends a broadcast message using the socket on port `<port>` to all computers on the local network. The broadcast message contains the calculated average.

3. **If the value is `-1`:**
    - The process performs the following actions sequentially:
        1. Prints the value `-1` to the console.
        2. Sends a broadcast message using the socket on port `<port>` to all computers on the local network. The broadcast message contains the value `-1`.
        3. Closes the socket and terminates.

---

### Slave Mode

The application in **slave mode** creates a UDP socket on a randomly assigned port (determined by the operating system) and sends a message to the process running on the same machine at port `<port>`. The message contains the `<number>` parameter value. After sending the message, the slave process terminates.

---
