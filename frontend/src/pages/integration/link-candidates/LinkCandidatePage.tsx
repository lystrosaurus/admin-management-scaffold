import { useState, useEffect, useCallback } from 'react';
import {
  Card,
  Table,
  Button,
  Space,
  Tag,
  message,
  Popconfirm,
  type TablePaginationConfig,
} from 'antd';
import { ReloadOutlined, CheckOutlined, CloseOutlined } from '@ant-design/icons';
import {
  listPendingLinkCandidates,
  confirmLinkCandidate,
  rejectLinkCandidate,
} from '@/api/integration';
import type { LinkCandidateVO } from '@/types/integration';
import dayjs from 'dayjs';

/** 候选类型选项 */
const candidateTypeOptions = [
  { label: '员工', value: 'EMPLOYEE' },
  { label: '组织单元', value: 'ORG_UNIT' },
];

/**
 * 渲染候选状态标签
 */
const renderStatusTag = (status: string) => {
  const map: Record<string, { color: string; text: string }> = {
    PENDING: { color: 'gold', text: '待处理' },
    CONFIRMED: { color: 'green', text: '已确认' },
    REJECTED: { color: 'red', text: '已拒绝' },
  };
  const item = map[status] || { color: 'default', text: status };
  return <Tag color={item.color}>{item.text}</Tag>;
};

/** 身份匹配候选（待处理）页面 */
const LinkCandidatePage = () => {
  // 列表数据
  const [dataSource, setDataSource] = useState<LinkCandidateVO[]>([]);
  const [loading, setLoading] = useState(false);
  const [current, setCurrent] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [processingId, setProcessingId] = useState<number | null>(null);

  /**
   * 获取待处理候选列表
   */
  const fetchCandidates = useCallback(async () => {
    setLoading(true);
    try {
      const result = await listPendingLinkCandidates();
      setDataSource(result);
    } catch (error) {
      message.error((error as Error).message || '获取待处理候选列表失败');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchCandidates();
  }, [fetchCandidates]);

  /**
   * 处理分页变化
   */
  const handleTableChange = (pagination: TablePaginationConfig) => {
    setCurrent(pagination.current || 1);
    setPageSize(pagination.pageSize || 10);
  };

  /**
   * 确认候选
   */
  const handleConfirm = async (id: number) => {
    setProcessingId(id);
    try {
      await confirmLinkCandidate(id);
      message.success('已确认');
      fetchCandidates();
    } catch (error) {
      message.error((error as Error).message || '确认失败');
    } finally {
      setProcessingId(null);
    }
  };

  /**
   * 拒绝候选
   */
  const handleReject = async (id: number) => {
    setProcessingId(id);
    try {
      await rejectLinkCandidate(id);
      message.success('已拒绝');
      fetchCandidates();
    } catch (error) {
      message.error((error as Error).message || '拒绝失败');
    } finally {
      setProcessingId(null);
    }
  };

  /** 表格列定义 */
  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: '外部主体ID',
      dataIndex: 'sourcePrincipalId',
      key: 'sourcePrincipalId',
      width: 120,
    },
    {
      title: '候选类型',
      dataIndex: 'candidateType',
      key: 'candidateType',
      width: 110,
      render: (text: string) =>
        candidateTypeOptions.find((o) => o.value === text)?.label || text,
    },
    {
      title: '候选目标ID',
      dataIndex: 'candidateId',
      key: 'candidateId',
      width: 120,
    },
    {
      title: '匹配分数',
      dataIndex: 'score',
      key: 'score',
      width: 100,
      render: (value: number | undefined) => (value ?? '-'),
    },
    {
      title: '匹配原因',
      dataIndex: 'reason',
      key: 'reason',
      width: 220,
      render: (text: string | undefined) => text || '-',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => renderStatusTag(status),
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
      render: (text: string | undefined) =>
        text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : '-',
    },
    {
      title: '操作',
      key: 'action',
      width: 160,
      render: (_: unknown, record: LinkCandidateVO) => (
        <Space size="small">
          <Popconfirm
            title="确认候选"
            description="确定要确认该匹配候选吗？"
            onConfirm={() => handleConfirm(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button
              type="link"
              size="small"
              icon={<CheckOutlined />}
              loading={processingId === record.id}
            >
              确认
            </Button>
          </Popconfirm>
          <Popconfirm
            title="拒绝候选"
            description="确定要拒绝该匹配候选吗？"
            onConfirm={() => handleReject(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button type="link" size="small" danger icon={<CloseOutlined />}>
              拒绝
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <Card>
      {/* 工具栏 */}
      <div style={{ marginBottom: 16 }}>
        <Button icon={<ReloadOutlined />} onClick={fetchCandidates}>
          刷新
        </Button>
      </div>

      {/* 表格 */}
      <Table
        rowKey="id"
        columns={columns}
        dataSource={dataSource}
        loading={loading}
        pagination={{
          current,
          pageSize,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total) => `共 ${total} 条`,
        }}
        onChange={handleTableChange}
        scroll={{ x: 1200 }}
      />
    </Card>
  );
};

export default LinkCandidatePage;
