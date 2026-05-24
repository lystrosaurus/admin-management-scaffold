import { Spin } from 'antd';

/**
 * 加载中组件
 */
export const LoadingSpinner = () => {
  return (
    <div style={{
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      height: '100vh',
    }}>
      <Spin size="large" />
    </div>
  );
};
