import {DefaultPorts, Environment} from './_api';

export interface State {
  currentEnvironment: Environment;
  ping(port: DefaultPorts);
}