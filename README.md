# Multithreaded Car Park System

A client-server simulation of a car park written in Java, demonstrating socket-based networking, multithreading, and mutual exclusion (locking) over shared state.

Originally built as a university project. It models a single car park with two entrances and two exits, each run as an independent client process that communicates with a central server over TCP sockets.

## Overview

- **`CarParkServer`** — starts a `ServerSocket` on port `4545` and accepts incoming client connections. For each connection, it spins up a dedicated `CarparkServerThread` to handle that client.
- **`CarparkServerThread`** — one thread per connected client. Reads requests from its client, asks the shared state object to process them, and writes the response back.
- **`SharedCarparkState`** — holds the shared car count (and a queue for cars waiting when the car park is full) and exposes a manual locking mechanism (`acquireLock()` / `releaseLock()`) plus the core logic (`processInput()`) that all four client threads compete to use safely.
- **`EntranceClient1` / `EntranceClient2`** — simulate cars arriving and requesting entry.
- **`ExitClient1` / `ExitClient2`** — simulate cars leaving and requesting exit.

Each client is a separate Java process (you run 4 of them, plus the server, as 5 separate terminal windows/processes) that connects over a socket and sends the command:

```
Do my action!
```

The server processes the request against the shared car count and replies with a status message (e.g. `"Car has entered from Entrance 1"` or `"The carpark is full. Please wait."`).

## Concurrency design

The interesting part of this project isn't the car park logic itself — it's how access to the **shared car count** is protected when four independent client threads can hit it at the same time.

- `SharedCarparkState` implements its own manual lock rather than relying purely on `synchronized` blocks around the caller's code:
  - `acquireLock()` uses a `while (accessing) { wait(); }` loop (guarding against spurious wakeups) and a `threadsWaiting` counter.
  - `releaseLock()` flips the flag and calls `notifyAll()` to wake any threads blocked in `wait()`.
- Each `CarparkServerThread` calls `acquireLock()` before touching shared state, and `releaseLock()` immediately after — a manual mutex pattern that mirrors what `synchronized` gives you for free, but implemented explicitly to show the underlying mechanics.
- `processInput()` itself is also marked `synchronized` as a second layer of protection.

This is a good example of the classic **producer/consumer-style mutual exclusion pattern**, applied to a shared counter instead of a queue.

## How to compile and run

You'll need a JDK installed. From the project directory:

```bash
javac *.java
```

Then, in **five separate terminal windows** (order matters — see Known Limitations below):

```bash
# Terminal 1
java CarParkServer

# Terminal 2
java EntranceClient1

# Terminal 3
java EntranceClient2

# Terminal 4
java ExitClient1

# Terminal 5
java ExitClient2
```

In each client terminal, type:

```
Do my action!
```

and press Enter to send a request to the server and see the response.

## Known limitations

This was a learning project, and a few rough edges are worth flagging (and are good discussion points on what a production version would need to fix):

- **Fixed client connection order** — `CarParkServer`'s main loop assumes clients always connect in the exact order Entrance1 → Entrance2 → Exit1 → Exit2, forever. A more robust version would identify clients by a handshake message rather than connection order.
- **No graceful shutdown or disconnect handling** — if a client disconnects, the server doesn't recover cleanly.
- **Mixing `static` and instance state** — `mySharedVariable` in `SharedCarparkState` is `static`, even though it's set from an instance constructor and read via instance methods. It works because the project only ever creates one `SharedCarparkState`, but it's not a pattern to replicate in real code.
- **Manual lock instead of `synchronized`/`ReentrantLock`** — kept intentionally in this version to make the underlying wait/notify mechanics visible, but a production version would likely use `java.util.concurrent` primitives (e.g. `ReentrantLock`, `Semaphore`) instead of hand-rolled locking.

## Possible extensions

- Replace the hardcoded connection order with client self-identification on connect.
- Use a `Semaphore` to represent car park capacity directly instead of a manual counter + lock.
- Add a proper client-side UI instead of raw terminal input.
- Add automated tests simulating concurrent entry/exit requests.
