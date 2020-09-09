// TODO: review

// based on ws.d.ts
declare module "ws" {
    class WebSocket extends events.EventEmitter {
        constructor(address: string);
        connect(): boolean;
    }

    module WebSocket {
        export class Server {
            start();
        }
    }

    export = WebSocket;
}
declare module events {
    class EventEmitter {

    }
}