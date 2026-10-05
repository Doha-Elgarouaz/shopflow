import { useQuery } from '@tanstack/react-query';
import api from '@/lib/api';
import { ApiPage, Product } from '@/lib/types';

export const useProducts = (page = 0, size = 12) =>
  useQuery<ApiPage<Product>>({
    queryKey: ['products', page, size],
    queryFn: () => api.get(`/api/products?page=${page}&size=${size}`).then((r) => r.data),
  });

export const useProduct = (id: number) =>
  useQuery<Product>({
    queryKey: ['product', id],
    queryFn: () => api.get(`/api/products/${id}`).then((r) => r.data),
    enabled: !!id,
  });

export const useProductsByCategory = (category: string, page = 0, size = 12) =>
  useQuery<ApiPage<Product>>({
    queryKey: ['products', 'category', category, page],
    queryFn: () =>
      api.get(`/api/products/category/${category}?page=${page}&size=${size}`).then((r) => r.data),
    enabled: !!category,
  });
