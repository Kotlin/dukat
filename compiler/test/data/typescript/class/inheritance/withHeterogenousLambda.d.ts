declare  interface TLSSocket {}

declare class Server {
  once(event: "secureConnection", listener: (tlsSocket: TLSSocket) => void): this;
  listeners(event: string | symbol): Function[];
}

type MessageListener = (message: any, sendHandle: any) => void;

declare class HttpServer2  extends Server {
  once(event: "unknownProtocol", listener: (socket: TLSSocket) => void): this;
  listeners(event: "message"): MessageListener[];
}