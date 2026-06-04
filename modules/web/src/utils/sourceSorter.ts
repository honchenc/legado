import type { Source } from '@/source'

export type SortMode = 'default' | 'name' | 'url' | 'weight' | 'update' | 'respond' | 'enable'

export function sortSources(sources: Source[], mode: SortMode, ascending: boolean): Source[] {
  const result = [...sources]
  switch (mode) {
    case 'default':
      result.sort((a, b) => a.customOrder - b.customOrder)
      break
    case 'name':
      result.sort((a, b) => a.bookSourceName.localeCompare(b.bookSourceName, 'zh-CN-u-co-pinyin'))
      break
    case 'url':
      result.sort((a, b) => (a.bookSourceUrl || '').localeCompare(b.bookSourceUrl || ''))
      break
    case 'weight':
      result.sort((a, b) => a.weight - b.weight)
      break
    case 'update':
      result.sort((a, b) => b.lastUpdateTime - a.lastUpdateTime)
      break
    case 'respond':
      result.sort((a, b) => a.respondTime - b.respondTime)
      break
    case 'enable':
      result.sort((a, b) => {
        if (a.enabled !== b.enabled) return Number(b.enabled) - Number(a.enabled)
        return a.bookSourceName.localeCompare(b.bookSourceName, 'zh-CN-u-co-pinyin')
      })
      break
  }
  return ascending ? result : result.reverse()
}
