import { TestBed } from '@angular/core/testing';

import { OutsourceService } from './outsource-component.service';

describe('OursourceService', () => {
  let service: OutsourceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OutsourceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
