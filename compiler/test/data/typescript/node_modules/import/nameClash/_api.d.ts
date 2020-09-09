declare namespace API {
  interface Module {
    children: Module[];
  }
}

interface ApiModule extends API.Module {}