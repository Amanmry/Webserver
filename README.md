# How Multithreaded Server is Implemented to Handle Ton's of Request & Serve them in fractions of Seconds

1. Limiting the number of Threads

2. Add Thread Pool to save on Thread Creation Time

3. Connection time out

4. TCP Backlog Queue Configurations

---

## 1. Limiting the number of Threads

**Limiting the number of threads** in a multithreaded web server refers to controlling how many threads (independent units of execution) can be active at the same time. This is important for managing system resources efficiently and avoiding overloading the server.

### Key Concept:
- **Threads**: A web server often handles multiple client requests simultaneously using threads. Each request may be processed in a separate thread, allowing the server to manage multiple clients concurrently.
  
- **Why Limit Threads?** 
  - **Resource Management**: Threads consume system resources like memory and CPU. Too many threads can lead to performance degradation due to excessive resource consumption or thread contention.
  - **Prevent Overloading**: If there are too many active threads, it can overwhelm the server, slowing it down or causing it to crash. Limiting the number of threads ensures that the server remains responsive and stable.
  
- **How it Works**:
  - **Thread Pooling**: A common approach is to use a thread pool, which is a collection of pre-created threads that can be reused to handle requests. The number of threads in the pool is capped at a limit to control resource usage.
  - **Queueing Requests**: If the number of incoming requests exceeds the available threads, requests can be queued until a thread becomes available, preventing the server from being overwhelmed.

### Example:
Imagine a web server that can handle a maximum of 10 requests at once. If 15 requests come in at the same time:
1. The first 10 requests are assigned to available threads.
2. The remaining 5 requests are put in a queue.
3. Once a thread finishes a request, it picks up the next request from the queue, ensuring all requests are eventually processed without overloading the server.

In short, **limiting the number of threads** helps the server handle requests efficiently while ensuring that it doesn't run out of resources or crash due to too many concurrent tasks.

---

## 2. Add Thread Pool to save on Thread Creation Time

- [How Threads Creation Happens & Time Consumption while creation of new Thread](Theory/7.Thread_Creation_Process_&_Time_Consumption.md)

**Adding a thread pool** in a multithreaded web server is a strategy to improve efficiency by reusing threads, instead of creating new threads for every incoming request. This helps save time and resources, particularly when dealing with a high volume of requests.

### Key Concept:
- **Thread Pool**: A **thread pool** is a collection of pre-created, reusable threads that sit idle, waiting to handle tasks (like processing web requests). When a request comes in, instead of creating a new thread from scratch, the server grabs an available thread from the pool to handle the request.

- **Why Use a Thread Pool?**
  - **Saving Time on Thread Creation**: Creating and destroying threads can be slow and resource-intensive. A thread pool avoids this overhead by reusing threads. When a thread finishes handling a request, it doesn't get destroyed; it returns to the pool to be reused for the next request.
  - **Resource Management**: By limiting the number of threads in the pool, the server can prevent resource overload (e.g., too much memory or CPU usage). This helps keep the server stable and responsive even under heavy load.

### How it Works:
1. **Thread Pool Initialization**: The server creates a fixed number of threads (the pool size). These threads are idle, waiting for tasks.
2. **Incoming Request**: When a new client request arrives:
   - The server checks if any threads in the pool are available.
   - If an idle thread is available, it is assigned to handle the request.
3. **Thread Reuse**: After a thread finishes handling the request, it doesn’t get destroyed. Instead, it returns to the pool, ready to handle another request.

### Example:
Consider a web server with a thread pool of 5 threads:
- 5 clients send requests to the server at once.
- The server assigns each of the 5 requests to an available thread.
- If more than 5 clients arrive, their requests are queued.
- Once a thread finishes a task, it becomes available again, allowing the next queued request to be handled.

### Benefits:
- **Faster Response**: Reusing threads avoids the overhead of creating and destroying threads for each request.
- **Controlled Resource Usage**: The pool size can be adjusted to prevent resource exhaustion (e.g., too many threads consuming memory or CPU).
- **Improved Performance**: The server can handle more requests with fewer resources by efficiently managing its threads.

In summary, adding a thread pool in a multithreaded web server is about **pre-creating threads** and **reusing them** for multiple tasks to reduce the cost of thread creation and improve performance.

---

## 3. Connection time out

### **Connection Timeout for Threads in Multithreaded Web Servers**

In the context of a multithreaded web server, **connection timeout for threads** refers to the maximum amount of time a thread will wait for a connection or a response before it gives up and terminates the request. This is a way to ensure that threads don't get stuck waiting for responses indefinitely, which could block resources and slow down the server.

### **Why Connection Timeout Matters:**
In a web server, each incoming request is typically handled by a separate thread. If a request takes too long (for example, due to a slow client, network issues, or a problem on the backend), that thread may end up waiting forever, blocking resources (such as memory or CPU) that could be used for processing other requests.

By setting a **timeout** on how long a thread can wait for a response from a client or backend, the server ensures that threads:
- Don’t get stuck indefinitely waiting for unresponsive clients or resources.
- Free up resources promptly to handle other requests.
- Improve the overall responsiveness and reliability of the server.

### **How Connection Timeout Works in a Web Server:**

1. **Request Handling**: When a client sends a request, a thread is allocated to process the request.
   
2. **Wait for Response**: The thread may have to wait for a certain amount of time to receive the full data or a response from the client, the database, or an external service.

3. **Timeout Check**: During this wait time, the server checks periodically if the thread has exceeded the predefined timeout period.
   - If the thread exceeds the timeout, the server will cancel the request and terminate the thread.
   - If the request completes within the timeout period, the thread finishes processing the request and returns a response to the client.

4. **Thread Cleanup**: After a thread times out or completes the request, it returns to the thread pool (if thread pooling is used) or gets terminated, freeing up resources for new incoming requests.

### **Example Scenario:**

Suppose a web server has a connection timeout set to 30 seconds:
- A thread is created to handle an incoming request.
- The thread waits for a response from a backend database or an external API.
- If the response isn't received within 30 seconds (due to a slow database, network issue, or an unresponsive external service), the thread is "timed out."
- The server will terminate the thread, potentially log an error, and move on to other requests. The client may get an error message indicating a timeout.

### **Why It’s Important:**

- **Prevents Resource Blocking**: If a thread waits too long for a response, it could block system resources (like memory and CPU) that could otherwise be used to process other requests.
- **Improves Server Performance**: Setting a connection timeout ensures that the server can handle new incoming requests in a timely manner instead of getting bogged down by requests that take too long to complete.
- **Better Client Experience**: Clients won’t have to wait indefinitely for a response. They’ll get a timeout error if a request cannot be completed in a reasonable time, which improves the user experience by providing clear feedback.

### **Example in Practice:**

Imagine a server with 10 threads and a connection timeout of 5 seconds:
- 10 clients make simultaneous requests to the server.
- If one client’s request is stuck waiting for a slow response (e.g., from an external service), the thread handling that request will time out after 5 seconds and stop waiting.
- The server can then move on to the next request, ensuring other clients are not delayed by the slow request.

### **Summary:**

**Connection timeout for threads** is a safeguard in a multithreaded web server that ensures each thread doesn't wait too long for a response. It helps in:
- Avoiding resource blocking by terminating threads that take too long to get a response.
- Maintaining a responsive server that can handle incoming requests efficiently.
- Improving overall server stability and user experience by providing clear feedback when something goes wrong (e.g., a timeout).

---

## 4. TCP Backlog Queue Configurations

### **TCP Backlog Queue in Multithreaded Web Servers**

The **TCP backlog queue** is a concept used in networking to manage incoming connection requests to a server before they are fully accepted. In the context of a **multithreaded web server**, the backlog queue helps manage the initial stages of a connection (before the request is processed by the server) and ensures the server can handle incoming requests efficiently without getting overwhelmed.

- [Understand about TCP Backlog Queue](Theory/9.TCP_Backlog_Queue.md)

### **How It Works:**

When a client tries to connect to a web server using the TCP protocol, the connection process goes through several stages:
1. **SYN Request**: The client sends a **SYN (synchronize)** message to initiate the connection.
2. **SYN-ACK Response**: The server responds with a **SYN-ACK** message to acknowledge the request.
3. **ACK Completion**: Finally, the client sends an **ACK** message to complete the handshake.

At this point, the server needs to accept the connection and start processing the client's request. However, the server may not be able to process the connection right away due to high load or limited resources. To manage this situation, the **TCP backlog queue** holds the pending connections that are in the process of completing the handshake but haven't been fully accepted by the server yet.

### **Backlog Queue and Thread Allocation:**

In a multithreaded server:
- The **TCP backlog queue** is a temporary holding area for incoming connection requests. The server doesn't immediately assign a thread to handle the request until the connection is fully established (i.e., the three-way handshake is completed).
- Once a connection is accepted, the server assigns it to an available worker thread to process the request.

### **Backlog Queue Size Configuration:**

The **size of the TCP backlog queue** determines how many **half-open connections** (connections that have completed the handshake but aren't yet fully processed) the server can hold at once. The backlog size is configured in the server's TCP settings and can be adjusted based on expected traffic.

- **Small Backlog Queue**: If the backlog queue is too small, the server may reject incoming connection attempts if it is already full. This can lead to **connection failures** for clients and poor user experience.
  
- **Large Backlog Queue**: A larger backlog queue can hold more incoming requests, giving the server more time to process them. However, if the server's threads are limited, a large backlog could lead to long delays in request processing.

### **Why Backlog Queue is Important in Multithreaded Web Servers:**

1. **Managing High Traffic**: When there are too many incoming connection requests in a short period (e.g., a surge of traffic), the backlog queue helps prevent the server from rejecting requests outright. It allows the server to buffer connections until it has resources (i.e., threads) available to handle them.

2. **Avoiding Overload**: By controlling the size of the backlog queue, the server can avoid being overloaded. If the queue is too large and the server is too slow to accept connections, it could eventually consume too many resources (e.g., memory), affecting performance.

3. **Controlling Resource Use**: In a multithreaded web server, the backlog queue allows the server to control the flow of incoming connections and balance resource usage (like threads and memory). This ensures the server does not start too many connections at once, which could overwhelm the available threads.

### **Example:**

Imagine a web server running with the following configuration:
- The **backlog queue size** is set to 100.
- The server can handle **50 threads** concurrently, each processing one client request at a time.

1. **First 50 Connections**: The server accepts the first 50 connections and assigns them to available threads to process requests.
   
2. **Next 50 Connections**: The next 50 incoming connections are placed in the backlog queue. The server holds these connections until it has available threads to process them.

3. **More than 100 Connections**: If more than 100 connections arrive and the queue is full, the server will either:
   - **Reject the connections** (depending on configuration), or
   - **Drop the connections**, leading to clients receiving connection errors.

### **Configuring the Backlog Queue:**

The size of the backlog queue is typically configured when the server starts and can be set using server or operating system settings. For example, in a Linux-based system, the backlog size can be set using the `listen()` system call, which tells the kernel how many connections to allow in the backlog before rejecting new ones.

### **Summary:**

In a multithreaded web server, the **TCP backlog queue** holds incoming connection requests that have completed the initial TCP handshake but haven't yet been fully processed. The size of this queue is critical to managing server load:
- **Small queues** may result in rejected connections during traffic spikes.
- **Larger queues** allow more time to handle incoming requests, but can lead to delays if the server doesn’t have enough threads to process them.

Configuring the backlog queue helps balance incoming connection traffic and optimizes server performance without overloading resources.

---
