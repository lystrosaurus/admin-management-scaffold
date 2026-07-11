import { useState, useEffect, useCallback } from 'react';
import {
  Card,
  Table,
  Button,
  Space,
  Upload,
  Select,
  Input,
  Popconfirm,
  message,
  type TablePaginationConfig,
  type UploadProps,
} from 'antd';
import { UploadOutlined, ReloadOutlined, DownloadOutlined, DeleteOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { listFiles, uploadFile, deleteFile, downloadFile } from '@/api/file';
import type { FileItem } from '@/types/file';

/** 业务类型选项 */
const bizTypeOptions = [
  { label: '通用', value: 'GENERAL' },
  { label: '头像', value: 'AVATAR' },
  { label: '文档', value: 'DOCUMENT' },
];

/**
 * 格式化文件大小
 */
const formatSize = (bytes: number): string => {
  if (!bytes && bytes !== 0) return '-';
  if (bytes < 1024) return `${bytes} B`;
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(2)} KB`;
  return `${(bytes / 1024 / 1024).toFixed(2)} MB`;
};

/**
 * 文件管理页面
 */
const FileManagePage = () => {
  const [dataSource, setDataSource] = useState<FileItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [current, setCurrent] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchParams, setSearchParams] = useState<{ originalName?: string; bizType?: string }>({});
  const [uploading, setUploading] = useState(false);
  const [bizType, setBizType] = useState<string>('GENERAL');

  const fetchFiles = useCallback(async () => {
    setLoading(true);
    try {
      const params = {
        page: current,
        size: pageSize,
        ...searchParams,
      };
      const result = await listFiles(params);
      setDataSource(result.items);
      setTotal(result.total);
    } catch (error) {
      message.error((error as Error).message || '获取文件列表失败');
    } finally {
      setLoading(false);
    }
  }, [current, pageSize, searchParams]);

  useEffect(() => {
    fetchFiles();
  }, [fetchFiles]);

  const handleSearch = (originalName: string) => {
    setCurrent(1);
    setSearchParams({ originalName: originalName || undefined, bizType });
  };

  const handleReset = () => {
    setCurrent(1);
    setBizType('GENERAL');
    setSearchParams({});
  };

  const handleTableChange = (pagination: TablePaginationConfig) => {
    setCurrent(pagination.current || 1);
    setPageSize(pagination.pageSize || 10);
  };

  /**
   * 自定义上传：调用后端 POST /api/app/files
   */
  const customRequest: UploadProps['customRequest'] = async ({ file, onSuccess, onError }) => {
    setUploading(true);
    try {
      await uploadFile(file as File, bizType);
      message.success('上传成功');
      onSuccess?.(null);
      fetchFiles();
    } catch (error) {
      message.error((error as Error).message || '上传失败');
      onError?.(error as Error);
    } finally {
      setUploading(false);
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await deleteFile(id);
      message.success('删除成功');
      fetchFiles();
    } catch (error) {
      message.error((error as Error).message || '删除失败');
    }
  };

  const handleDownload = async (record: FileItem) => {
    try {
      await downloadFile(record.id, record.originalName);
    } catch (error) {
      message.error((error as Error).message || '下载失败');
    }
  };

  const columns = [
    {
      title: '原文件名',
      dataIndex: 'originalName',
      key: 'originalName',
      ellipsis: true,
      render: (text: string) => text || '-',
    },
    {
      title: '大小',
      dataIndex: 'sizeBytes',
      key: 'sizeBytes',
      width: 120,
      render: (size: number) => formatSize(size),
    },
    { title: '扩展名', dataIndex: 'ext', key: 'ext', width: 90, render: (t: string) => t || '-' },
    {
      title: '业务类型',
      dataIndex: 'bizType',
      key: 'bizType',
      width: 110,
      render: (t: string) => t || '-',
    },
    {
      title: '上传人',
      dataIndex: 'uploaderName',
      key: 'uploaderName',
      width: 120,
      render: (t: string | undefined, record: FileItem) => t || `ID:${record.uploaderId}`,
    },
    {
      title: '上传时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
      render: (text: string) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : '-'),
    },
    {
      title: '操作',
      key: 'action',
      width: 160,
      render: (_: unknown, record: FileItem) => (
        <Space size="small">
          <Button type="link" size="small" icon={<DownloadOutlined />} onClick={() => handleDownload(record)}>
            下载
          </Button>
          <Popconfirm
            title="确认删除"
            description="确定要删除该文件吗？"
            onConfirm={() => handleDelete(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <Card>
      <Space style={{ marginBottom: 16 }} wrap>
        <Select
          value={bizType}
          onChange={setBizType}
          options={bizTypeOptions}
          style={{ width: 140 }}
          placeholder="业务类型"
        />
        <Upload
          showUploadList={false}
          customRequest={customRequest}
          disabled={uploading}
          accept="*"
        >
          <Button type="primary" icon={<UploadOutlined />} loading={uploading}>
            上传文件
          </Button>
        </Upload>
        <Input.Search
          placeholder="按文件名搜索"
          allowClear
          onSearch={handleSearch}
          style={{ width: 220 }}
        />
        <Button icon={<ReloadOutlined />} onClick={handleReset}>
          重置
        </Button>
      </Space>

      <Table
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
    </Card>
  );
};

export default FileManagePage;
