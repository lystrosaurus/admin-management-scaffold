import { useState, useEffect } from 'react';
import {
  Card,
  Tabs,
  Table,
  Form,
  Input,
  DatePicker,
  Button,
  Space,
  Tag,
  message,
  type TablePaginationConfig,
  type TableColumnsType,
} from 'antd';
import { SearchOutlined, ReloadOutlined } from '@ant-design/icons';
import type { PageResult } from '@/types/api';
import dayjs, { type Dayjs } from 'dayjs';
import { getLoginLogs, getOperationLogs } from '@/api/auditLog';
import type { LoginLog, OperationLog } from '@/types/auditLog';

/**
 * 通用日志表格（带搜索 + 分页）
 * 两个页签（登录日志 / 操作日志）共用同一套交互范式
 */
interface LogTableProps<T> {
  fetcher: (params: Record<string, unknown>) => Promise<PageResult<T>>;
  columns: TableColumnsType<T>;
}

const LogTable = <T extends { id: number }>({ fetcher, columns }: LogTableProps<T>) => {
  const [dataSource, setDataSource] = useState<T[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [current, setCurrent] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchParams, setSearchParams] = useState<Record<string, unknown>>({});
  const [form] = Form.useForm();

  useEffect(() => {
    // 首屏/翻页加载：setState 仅在 await 之后执行，并带取消保护
    let ignore = false;
    void (async () => {
      try {
        const params: Record<string, unknown> = {
          page: current,
          size: pageSize,
          ...searchParams,
        };
        const result = await fetcher(params);
        if (ignore) return;
        setDataSource(result.items);
        setTotal(result.total);
      } catch (error) {
        if (!ignore) message.error((error as Error).message || '获取日志失败');
      } finally {
        if (!ignore) setLoading(false);
      }
    })();
    return () => {
      ignore = true;
    };
  }, [fetcher, current, pageSize, searchParams]);

  const handleSearch = () => {
    const range = form.getFieldValue('timeRange') as [Dayjs, Dayjs] | undefined;
    setCurrent(1);
    setSearchParams({
      username: form.getFieldValue('username') || undefined,
      startTime: range?.[0] ? range[0].toISOString() : undefined,
      endTime: range?.[1] ? range[1].toISOString() : undefined,
    });
  };

  const handleReset = () => {
    form.resetFields();
    setCurrent(1);
    setSearchParams({});
  };

  const handleTableChange = (pagination: TablePaginationConfig) => {
    setCurrent(pagination.current || 1);
    setPageSize(pagination.pageSize || 10);
  };

  return (
    <div>
      <Form form={form} layout="inline" style={{ marginBottom: 16 }}>
        <Form.Item name="username">
          <Input placeholder="用户名" allowClear style={{ width: 160 }} />
        </Form.Item>
        <Form.Item name="timeRange" label="时间范围">
          <DatePicker.RangePicker showTime />
        </Form.Item>
        <Form.Item>
          <Space>
            <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
              搜索
            </Button>
            <Button icon={<ReloadOutlined />} onClick={handleReset}>
              重置
            </Button>
          </Space>
        </Form.Item>
      </Form>

      <Table<T>
        rowKey="id"
        columns={columns}
        dataSource={dataSource}
        loading={loading}
        pagination={{
          current,
          pageSize,
          total,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (t) => `共 ${t} 条`,
        }}
        onChange={handleTableChange}
      />
    </div>
  );
};

/** 登录日志列 */
const loginColumns: TableColumnsType<LoginLog> = [
  { title: '用户名', dataIndex: 'username', key: 'username', width: 140 },
  { title: 'IP', dataIndex: 'ip', key: 'ip', width: 140 },
  {
    title: '是否成功',
    dataIndex: 'success',
    key: 'success',
    width: 100,
    render: (success: boolean) => (
      <Tag color={success ? 'green' : 'red'}>{success ? '成功' : '失败'}</Tag>
    ),
  },
  {
    title: '登录时间',
    dataIndex: 'loginAt',
    key: 'loginAt',
    width: 180,
    render: (text: string) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : '-'),
  },
  {
    title: 'UA',
    dataIndex: 'userAgent',
    key: 'userAgent',
    ellipsis: true,
    render: (text: string) => text || '-',
  },
];

/** 操作日志列 */
const operationColumns: TableColumnsType<OperationLog> = [
  { title: '用户名', dataIndex: 'username', key: 'username', width: 140 },
  { title: '模块', dataIndex: 'module', key: 'module', width: 120 },
  { title: '动作', dataIndex: 'action', key: 'action', width: 140 },
  { title: '业务ID', dataIndex: 'bizId', key: 'bizId', width: 160 },
  {
    title: '操作时间',
    dataIndex: 'opAt',
    key: 'opAt',
    width: 180,
    render: (text: string) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : '-'),
  },
  { title: 'IP', dataIndex: 'ip', key: 'ip', width: 140 },
];

/**
 * 审计日志页面
 */
const AuditLogPage = () => {
  return (
    <Card>
      <Tabs
        defaultActiveKey="login"
        items={[
          {
            key: 'login',
            label: '登录日志',
            children: <LogTable<LoginLog> fetcher={getLoginLogs} columns={loginColumns} />,
          },
          {
            key: 'operation',
            label: '操作日志',
            children: (
              <LogTable<OperationLog> fetcher={getOperationLogs} columns={operationColumns} />
            ),
          },
        ]}
      />
    </Card>
  );
};

export default AuditLogPage;
