import { BrowserRouter } from 'react-router-dom';
import AppRoutes from './routes';

/**
 * 应用根组件
 * 挂载路由配置，由 main.tsx 提供 ConfigProvider 和 App 上下文
 */
function App() {
  return (
    <BrowserRouter>
      <AppRoutes />
    </BrowserRouter>
  );
}

export default App;
