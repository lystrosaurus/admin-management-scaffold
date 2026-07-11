import { useState, useEffect, useCallback } from 'react';
import {
  Card,
  Descriptions,
  Statistic,
  Button,
  Space,
  Tag,
  message,
} from 'antd';
import { ReloadOutlined } from '@ant-design/icons';
import { getAppInfo } from '@/api/ops';
import type { AppInfo } from '@/types/ops';

/**
 * 系统状态页面
 */
const SystemStatusPage = () => {
  const [info, setInfo] = useState<AppInfo | null>(null);
  const [loading, setLoading] = useState(false);

  const fetchInfo = useCallback(async () => {
    setLoading(true);
    try {
      const data = await getAppInfo();
      setInfo(data);
    } catch (error) {
      message.error((error as Error).message || '获取系统信息失败');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchInfo();
  }, [fetchInfo]);

  const statusColor = info?.status === 'UP' ? 'green' : info?.status === 'DOWN' ? 'red' : 'default';

  return (
    <Card
      title="系统状态"
      extra={
        <Button icon={<ReloadOutlined />} onClick={fetchInfo} loading={loading}>
          刷新
        </Button>
      }
    >
      <Space size={32} wrap style={{ marginBottom: 24 }}>
        <Statistic title="应用名" value={info?.name ?? '-'} />
        <Statistic title="版本" value={info?.version ?? '-'} />
        <Statistic title="Java 版本" value={info?.javaVersion ?? '-'} />
        <Statistic
          title="运行状态"
          valueRender={() =>
            info ? <Tag color={statusColor}>{info.status}</Tag> : '-'
          }
        />
      </Space>

      <Descriptions bordered column={2} loading={loading}>
        <Descriptions.Item label="应用名">{info?.name ?? '-'}</Descriptions.Item>
        <Descriptions.Item label="版本">{info?.version ?? '-'}</Descriptions.Item>
        <Descriptions.Item label="Java 版本">{info?.javaVersion ?? '-'}</Descriptions.Item>
        <Descriptions.Item label="运行状态">
          {info ? <Tag color={statusColor}>{info.status}</Tag> : '-'}
        </Descriptions.Item>
        <Descriptions.Item label="操作系统">{info?.osName ?? '-'}</Descriptions.Item>
        <Descriptions.Item label="系统架构">{info?.osArch ?? '-'}</Descriptions.Item>
        <Descriptions.Item label="运行 Profile">{info?.profile ?? '-'}</Descriptions.Item>
        <Descriptions.Item label="启动时间">{info?.startTime ?? '-'}</Descriptions.Item>
      </Descriptions>
    </Card>
  );
};

export default SystemStatusPage;
