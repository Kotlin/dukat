// based on https://github.com/DefinitelyTyped/DefinitelyTyped/blob/master/types/agora-rtc-sdk/index.d.ts
export interface ClientConfig  {
    proxyServer?: string;
    turnServer?: {
        turnServerURL: string;
        username: string;
        password: string;
        udpport: string;
        tcpport: string;
        forceturn: boolean;
    };
}

