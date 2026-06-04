import { describe, it, expect } from 'vitest'
import { sortSources } from '../utils/sourceSorter'

interface Source {
  bookSourceUrl: string
  bookSourceName: string
  customOrder: number
  enabled: boolean
  lastUpdateTime: number
  respondTime: number
  weight: number
  bookSourceGroup?: string
}

const a: Source = {
  bookSourceUrl: 'http://a.com',
  bookSourceName: 'A源',
  customOrder: 3,
  enabled: true,
  lastUpdateTime: 100,
  respondTime: 50,
  weight: 5,
}

const b: Source = {
  bookSourceUrl: 'http://b.com',
  bookSourceName: 'B源',
  customOrder: 1,
  enabled: false,
  lastUpdateTime: 200,
  respondTime: 150,
  weight: 3,
}

const c: Source = {
  bookSourceUrl: 'http://c.com',
  bookSourceName: 'C源',
  customOrder: 2,
  enabled: true,
  lastUpdateTime: 300,
  respondTime: 250,
  weight: 1,
}

describe('sourceSorter', () => {
  it('default sort by customOrder', () => {
    const result = sortSources([a, b, c], 'default', true)
    expect(result[0].bookSourceUrl).toBe('http://b.com')
    expect(result[1].bookSourceUrl).toBe('http://c.com')
    expect(result[2].bookSourceUrl).toBe('http://a.com')
  })

  it('sort by name ascending', () => {
    const result = sortSources([c, a, b], 'name', true)
    expect(result[0].bookSourceName).toBe('A源')
    expect(result[1].bookSourceName).toBe('B源')
    expect(result[2].bookSourceName).toBe('C源')
  })

  it('sort by name descending', () => {
    const result = sortSources([a, b, c], 'name', false)
    expect(result[0].bookSourceName).toBe('C源')
    expect(result[1].bookSourceName).toBe('B源')
    expect(result[2].bookSourceName).toBe('A源')
  })

  it('sort by url', () => {
    const result = sortSources([c, a, b], 'url', true)
    expect(result[0].bookSourceUrl).toBe('http://a.com')
    expect(result[1].bookSourceUrl).toBe('http://b.com')
    expect(result[2].bookSourceUrl).toBe('http://c.com')
  })

  it('sort by weight ascending', () => {
    const result = sortSources([a, b, c], 'weight', true)
    expect(result[0].weight).toBe(1)
    expect(result[1].weight).toBe(3)
    expect(result[2].weight).toBe(5)
  })

  it('sort by update time (ascending=true => most recent first)', () => {
    const result = sortSources([a, b, c], 'update', true)
    expect(result[0].lastUpdateTime).toBe(300)
    expect(result[1].lastUpdateTime).toBe(200)
    expect(result[2].lastUpdateTime).toBe(100)
  })

  it('sort by update time (ascending=false => oldest first)', () => {
    const result = sortSources([c, b, a], 'update', false)
    expect(result[0].lastUpdateTime).toBe(100)
    expect(result[1].lastUpdateTime).toBe(200)
    expect(result[2].lastUpdateTime).toBe(300)
  })

  it('sort by respond time ascending', () => {
    const result = sortSources([c, a, b], 'respond', true)
    expect(result[0].respondTime).toBe(50)
    expect(result[1].respondTime).toBe(150)
    expect(result[2].respondTime).toBe(250)
  })

  it('sort by enabled puts enabled first', () => {
    const result = sortSources([b, a], 'enable', true)
    expect(result[0].enabled).toBe(true)
    expect(result[1].enabled).toBe(false)
  })

  it('chinese pinyin ordering', () => {
    const result = sortSources(
      [
        { bookSourceName: '湖南', bookSourceUrl: 'http://hn.com' },
        { bookSourceName: '湖北', bookSourceUrl: 'http://hb.com' },
        { bookSourceName: '广东', bookSourceUrl: 'http://gd.com' },
      ] as Source[],
      'name',
      true,
    )
    expect(result[0].bookSourceName).toBe('广东')
    expect(result[1].bookSourceName).toBe('湖北')
    expect(result[2].bookSourceName).toBe('湖南')
  })

  it('empty list does not crash', () => {
    const result = sortSources([], 'default', true)
    expect(result).toEqual([])
  })

  it('descending reverses order', () => {
    const result = sortSources([a, b, c], 'name', false)
    expect(result[0].bookSourceName).toBe('C源')
    expect(result[2].bookSourceName).toBe('A源')
  })
})
