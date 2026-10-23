import { useRef, useState } from 'react'
import { createColumnHelper } from '@tanstack/react-table'
import { Download, RotateCw, Search, Trash2, Upload } from 'lucide-react'
import { toast } from 'sonner'
import dayjs from 'dayjs'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Card, CardContent } from '@/components/ui/card'
import { DataTable } from '@/components/data-table/DataTable'
import { ConfirmDialog } from '@/components/ConfirmDialog'
import { usePagedList } from '@/hooks/usePagedList'
import { coreTableFeatures } from '@/components/data-table/table-features'
import { listFiles, uploadFile, deleteFile, downloadFile } from '@/api/file'
import type { FileItem, FileQueryParams } from '@/types/file'

/** 业务类型选项 */
const BIZ_TYPE_OPTIONS = [
  { label: '通用', value: 'GENERAL' },
  { label: '头像', value: 'AVATAR' },
  { label: '文档', value: 'DOCUMENT' },
]

/** 格式化文件大小 */
const formatSize = (bytes: number): string => {
  if (!bytes && bytes !== 0) return '-'
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(2)} KB`
  return `${(bytes / 1024 / 1024).toFixed(2)} MB`
}

/** 文件管理页面 */
const FileManagePage = () => {
  const { rows, total, page, size, loading, setPage, setSize, setQuery, refresh } =
    usePagedList<FileItem, FileQueryParams>({
      fetcher: listFiles,
      initialSize: 10,
    })

  // 搜索条件
  const [searchName, setSearchName] = useState('')
  const [searchBizType, setSearchBizType] = useState<string>('')

  // 上传（业务类型选择 + 隐藏 file input）
  const [uploadBizType, setUploadBizType] = useState('GENERAL')
  const [uploading, setUploading] = useState(false)
  const fileInputRef = useRef<HTMLInputElement>(null)

  // 删除确认
  const [deleting, setDeleting] = useState<FileItem | null>(null)

  const handleSearch = () => {
    setQuery({
      originalName: searchName || undefined,
      bizType: searchBizType || undefined,
    })
  }

  const handleReset = () => {
    setSearchName('')
    setSearchBizType('')
    setQuery({})
  }

  const handleUpload = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    // 重置 input，保证同一文件可重复选择
    event.target.value = ''
    if (!file) return
    setUploading(true)
    try {
      await uploadFile(file, uploadBizType)
      toast.success('上传成功')
      refresh()
    } catch (error) {
      toast.error((error as Error).message || '上传失败')
    } finally {
      setUploading(false)
    }
  }

  const handleDownload = async (record: FileItem) => {
    try {
      await downloadFile(record.id, record.originalName)
    } catch (error) {
      toast.error((error as Error).message || '下载失败')
    }
  }

  const handleDelete = async () => {
    if (!deleting) return
    try {
      await deleteFile(deleting.id)
      toast.success('删除成功')
      refresh()
    } finally {
      setDeleting(null)
    }
  }

  /** 表格列定义 */
  const columnHelper = createColumnHelper<typeof coreTableFeatures, FileItem>()
  const columns = columnHelper.columns([
    columnHelper.accessor('originalName', {
      header: '原文件名',
      cell: (info) => <span className="block max-w-[260px] truncate">{info.getValue() || '-'}</span>,
    }),
    columnHelper.accessor('sizeBytes', {
      header: '大小',
      size: 120,
      cell: (info) => formatSize(info.getValue()),
    }),
    columnHelper.accessor('ext', {
      header: '扩展名',
      size: 90,
      cell: (info) => info.getValue() || '-',
    }),
    columnHelper.accessor('bizType', {
      header: '业务类型',
      size: 110,
      cell: (info) => info.getValue() || '-',
    }),
    columnHelper.accessor('uploaderName', {
      header: '上传人',
      size: 120,
      cell: (info) => info.getValue() || `ID:${info.row.original.uploaderId}`,
    }),
    columnHelper.accessor('createdAt', {
      header: '上传时间',
      size: 180,
      cell: (info) => (info.getValue() ? dayjs(info.getValue()).format('YYYY-MM-DD HH:mm:ss') : '-'),
    }),
    columnHelper.display({
      id: 'actions',
      header: '操作',
      size: 160,
      cell: (info) => (
        <div className="flex items-center gap-1">
          <Button
            variant="ghost"
            size="sm"
            className="h-7 px-2 text-primary"
            onClick={() => handleDownload(info.row.original)}
          >
            <Download className="size-3.5" />
            下载
          </Button>
          <Button
            variant="ghost"
            size="sm"
            className="h-7 px-2 text-destructive"
            onClick={() => setDeleting(info.row.original)}
          >
            <Trash2 className="size-3.5" />
            删除
          </Button>
        </div>
      ),
    }),
  ])

  return (
    <Card>
      <CardContent className="pt-6">
        <DataTable
          columns={columns}
          data={rows}
          total={total}
          page={page}
          pageSize={size}
          loading={loading}
          onPageChange={setPage}
          onPageSizeChange={setSize}
          getRowId={(row) => String(row.id)}
          toolbar={
            <div className="flex flex-wrap items-end gap-3">
              <div className="flex flex-col gap-1.5">
                <Label className="text-xs text-muted-foreground">上传业务类型</Label>
                <Select value={uploadBizType} onValueChange={setUploadBizType}>
                  <SelectTrigger className="w-[140px]">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {BIZ_TYPE_OPTIONS.map((option) => (
                      <SelectItem key={option.value} value={option.value}>
                        {option.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <Button type="button" disabled={uploading} onClick={() => fileInputRef.current?.click()}>
                <Upload className="size-4" />
                {uploading ? '上传中…' : '上传文件'}
              </Button>
              {/* 隐藏的文件选择器：直连后端 POST /api/app/files */}
              <input
                ref={fileInputRef}
                type="file"
                className="hidden"
                onChange={handleUpload}
              />
              <div className="flex flex-col gap-1.5">
                <Label className="text-xs text-muted-foreground">文件名</Label>
                <Input
                  placeholder="按文件名搜索"
                  className="w-[220px]"
                  value={searchName}
                  onChange={(event) => setSearchName(event.target.value)}
                  onKeyDown={(event) => event.key === 'Enter' && handleSearch()}
                />
              </div>
              <div className="flex flex-col gap-1.5">
                <Label className="text-xs text-muted-foreground">业务类型</Label>
                <Select
                  value={searchBizType || 'ALL'}
                  onValueChange={(value) => setSearchBizType(value === 'ALL' ? '' : value)}
                >
                  <SelectTrigger className="w-[140px]">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="ALL">全部</SelectItem>
                    {BIZ_TYPE_OPTIONS.map((option) => (
                      <SelectItem key={option.value} value={option.value}>
                        {option.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div className="flex gap-2">
                <Button onClick={handleSearch}>
                  <Search className="size-4" />
                  搜索
                </Button>
                <Button variant="outline" onClick={handleReset}>
                  <RotateCw className="size-4" />
                  重置
                </Button>
              </div>
            </div>
          }
        />
      </CardContent>

      {/* 删除确认 */}
      <ConfirmDialog
        open={!!deleting}
        title="确认删除"
        description={`确定要删除文件「${deleting?.originalName ?? ''}」吗？`}
        destructive
        onConfirm={handleDelete}
        onOpenChange={(open) => !open && setDeleting(null)}
      />
    </Card>
  )
}

export default FileManagePage
